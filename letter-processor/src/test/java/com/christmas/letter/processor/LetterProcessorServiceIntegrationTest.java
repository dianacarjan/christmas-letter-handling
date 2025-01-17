package com.christmas.letter.processor;

import com.christmas.letter.processor.dto.AddressMessage;
import com.christmas.letter.processor.dto.LetterMessage;
import com.christmas.letter.processor.helper.LetterUtils;
import com.christmas.letter.processor.helper.LocalStackTestContainer;
import com.christmas.letter.processor.mapper.LetterMapper;
import com.christmas.letter.processor.model.Letter;
import com.christmas.letter.processor.repository.LetterRepository;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource("classpath:config-test.properties")
class LetterProcessorServiceIntegrationTest extends LocalStackTestContainer {

    private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:000000000000:test-topic";
    private static final String LETTER_API_PATH = "/api/v1/christmas-letters";
    private static final String QUEUE_ENDPOINT="http://sqs.us-east-1.localhost:4566/000000000000/test-queue";
    private static final String DLQ_ENDPOINT="http://sqs.us-east-1.localhost:4566/000000000000/test-dlq";

    @Value("${letter-processor.aws.sqs.queue-url}")
    private String queueUrl;

    @Autowired
    private SnsTemplate snsTemplate;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private LetterRepository letterRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisTemplate<String, ?> redisTemplate;

    @BeforeEach
    void setup() throws IOException, InterruptedException {
        letterRepository.deleteAll();
        localStackContainer.execInContainer("awslocal", "sqs", "purge-queue", "--queue-url", QUEUE_ENDPOINT);

        Set<String> keys = redisTemplate.keys("60m-letter*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test()
    void givenPolling_whenListen_thenMessageReceived(CapturedOutput output) {
        // Arrange
        LetterMessage letterPayload = LetterUtils.generateLetterPayload();

        // Act
        snsTemplate.convertAndSend(TOPIC_ARN, letterPayload);

        // Assert
        await().atMost(Duration.ofSeconds(5)).until(() -> output.getAll().contains("Letter saved successfully!"));
        Optional<Letter> result = letterRepository.findById(letterPayload.getEmail());
        assertThat(result).isPresent();
    }

    @Test
    @WithMockUser(roles={"SANTA"})
    void givenGetRequest_whenGetAllMethod_thenReturnAllSavedLetters(CapturedOutput output) throws Exception {
        // Arrange
        LetterMessage letterPayload = LetterUtils.generateLetterPayload();

        // Act & Assert
        snsTemplate.convertAndSend(TOPIC_ARN, letterPayload);
        await().atMost(Duration.ofSeconds(10)).until(() -> output.getAll().contains("Letter saved successfully!"));

        mockMvc.perform(get(LETTER_API_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.offset").value(0));
        assertThat(redisTemplate.hasKey(String.format("60m-letter::%s", letterPayload.getEmail()))).isTrue();
        assertThat(Objects.requireNonNull(redisTemplate.keys("60m-letters:*")).size()).isOne();
    }

    @Test
    @WithMockUser(roles={"SANTA"})
    void givenEmail_whenGetByEmail_thenReturnSavedLetter(CapturedOutput output) throws Exception {
        // Arrange
        LetterMessage letterPayload = LetterUtils.generateLetterPayload();
        AddressMessage addressPayload = letterPayload.getAddress();

        // Act & Assert
        snsTemplate.convertAndSend(TOPIC_ARN, letterPayload);
        await().atMost(Duration.ofSeconds(10)).until(() -> output.getAll().contains("Letter saved successfully!"));
        mockMvc.perform(get(String.format("%s/{email}", LETTER_API_PATH), letterPayload.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(letterPayload.getEmail()))
                .andExpect(jsonPath("$.name").value(letterPayload.getName()))
                .andExpect(jsonPath("$.wishes").value(letterPayload.getWishes()))
                .andExpect(jsonPath("$.address.street").value(addressPayload.getStreet()))
                .andExpect(jsonPath("$.address.city").value(addressPayload.getCity()))
                .andExpect(jsonPath("$.address.state").value(addressPayload.getState()))
                .andExpect(jsonPath("$.address.zipCode").value(addressPayload.getZipCode()));

        assertThat(redisTemplate.hasKey(String.format("60m-letter::%s", letterPayload.getEmail()))).isTrue();
        assertThat(Objects.requireNonNull(redisTemplate.keys("60m-letters:*"))).isEmpty();
    }

    @Test
    void givenInvalidMessage_whenListen_thenSendMessageToDLQ(CapturedOutput output) {
        // Arrange
        sqsTemplate.send(queueUrl, "invalid_payload");

        // Act & Assert
        await().atMost(Duration.ofSeconds(2)).until(() -> output.getAll().contains("Invalid payload"));

        Iterable<Letter> result = letterRepository.findAll();
        assertThat(result.iterator().hasNext()).isFalse();

        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(4))
                .until(() ->
                        !localStackContainer.execInContainer(
                                "awslocal", "sqs", "receive-message",
                                "--queue-url", QUEUE_ENDPOINT).getStdout().isEmpty());

        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(4))
                .until(() ->
                        localStackContainer.execInContainer(
                                "awslocal", "sqs", "receive-message",
                                "--queue-url", QUEUE_ENDPOINT).getStdout().isEmpty());

        await()
                .pollInterval(Duration.ofSeconds(4))
                .atMost(Duration.ofSeconds(10))
                .until(() ->
                        sqsTemplate.receive(from -> from.queue(DLQ_ENDPOINT)).isPresent());
    }

    @Test
    void givenInvalidFieldsOfMessage_whenListen_thenValidationShouldFail(CapturedOutput output) {
        // Arrange
        String invalidEmail = "@@@#23";
        Letter newLetter = LetterUtils.generateLetter();
        newLetter.setEmail(invalidEmail);
        LetterMessage letterMessage = LetterMapper.INSTANCE.letterToLetterMessage(newLetter);

        // Act
        snsTemplate.convertAndSend(TOPIC_ARN, letterMessage);

        // Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains("Error processing message"));
        Optional<Letter> result = letterRepository.findById(invalidEmail);
        assertThat(result).isEmpty();
    }
}
