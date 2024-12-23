package com.christmas.letter.processor;

import com.christmas.letter.processor.dto.AddressMessage;
import com.christmas.letter.processor.dto.LetterMessage;
import com.christmas.letter.processor.helper.LetterUtils;
import com.christmas.letter.processor.helper.LocalStackTestContainer;
import com.christmas.letter.processor.model.Letter;
import com.christmas.letter.processor.repository.LetterRepository;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource("classpath:config-test.properties")
class LetterProcessorServiceIntegrationTest extends LocalStackTestContainer {

    private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:000000000000:test-topic";
    private static final String LETTER_API_PATH = "/api/v1/christmas-letters";

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

    @BeforeEach
    void setup() {
        letterRepository.deleteAll();
    }

    @Test()
    void givenPolling_whenListen_thenMessageReceived(CapturedOutput output) {
        // Arrange
        LetterMessage letterPayload = LetterUtils.generateLetterPayload();

        // Act
        snsTemplate.convertAndSend(TOPIC_ARN, letterPayload);

        // Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains("Letter saved successfully!"));
        Optional<Letter> result = letterRepository.findById(letterPayload.getEmail());
        assertThat(result).isPresent();
    }

    @Test
    void givenGetRequest_whenGetAllMethod_thenReturnAllSavedLetters(CapturedOutput output) throws Exception {
        // Arrange
        LetterMessage letterPayload = LetterUtils.generateLetterPayload();

        // Act & Assert
        snsTemplate.convertAndSend(TOPIC_ARN, letterPayload);
        await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains("Letter saved successfully!"));
        mockMvc.perform(get(LETTER_API_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.offset").value(0));
    }

    @Test
    void givenEmail_whenGetByEmail_thenReturnSavedLetter(CapturedOutput output) throws Exception {
        // Arrange
        LetterMessage letterPayload = LetterUtils.generateLetterPayload();
        AddressMessage addressPayload = letterPayload.getAddress();

        // Act & Assert
        snsTemplate.convertAndSend(TOPIC_ARN, letterPayload);
        await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains("Letter saved successfully!"));
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
    }

    // TODO: Fix required to remove @DirtiesContext
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void givenInvalidMessage_whenListen_thenConversionShouldFail(CapturedOutput output) {
        // Arrange
        sqsTemplate.send(queueUrl, "invalid_payload");

        // Act & Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains("Invalid payload"));
        Iterable<Letter> result = letterRepository.findAll();
        assertThat(result.iterator().hasNext()).isFalse();
    }

    @Test
    void givenInvalidFieldsOfMessage_whenListen_thenValidationShouldFail(CapturedOutput output) {
        // Arrange
        String invalidEmail = "@@@#23";
        Letter newLetter = LetterUtils.generateLetter();
        newLetter.setEmail(invalidEmail);
        LetterMessage letterMessage = LetterUtils.generateLetterPayload(newLetter);

        // Act
        snsTemplate.convertAndSend(TOPIC_ARN, letterMessage);

        // Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains("Error processing message"));
        Optional<Letter> result = letterRepository.findById(invalidEmail);
        assertThat(result).isEmpty();
    }

}
