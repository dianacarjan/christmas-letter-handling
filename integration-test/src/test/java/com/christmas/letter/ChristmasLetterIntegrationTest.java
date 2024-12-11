package com.christmas.letter;

import com.christmas.letter.helper.LetterUtils;
import com.christmas.letter.helper.LocalStackContainerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource("classpath:config-test.properties")
class ChristmasLetterIntegrationTest extends LocalStackContainerTest {
    @Autowired
    private MockMvc mockMvc;

    @Value("${letter-sender.aws.sns.topic-arn}")
    String topicArn;

    private static final String LETTER_CREATE_PATH = "/api/v1/christmas-letters";

    @Test
    void givenLetter_whenPublishingToSNS_thenMessageQueueIsReceived(CapturedOutput output) throws Exception {
        // Arrange
        String christmasLetter = LetterUtils.generateRandomChristmasLetter("test@example.com");

        // Act
        mockMvc.perform(post(LETTER_CREATE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(christmasLetter))
                .andExpect(status().isCreated());

        // Assert
        String expectedPublisherLog = String.format("Successfully published message to topic ARN: %s", topicArn);
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains(expectedPublisherLog));

        String expectedSubscriberLog = "Message received";
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains(expectedSubscriberLog));
    }

    @Test
    void givenInvalidLetter_whenSendingLetter_thenValidationFails() throws Exception {
        // Arrange
        String letter = LetterUtils.generateRandomChristmasLetter(null);

        // Act & Assert
        mockMvc.perform(post(LETTER_CREATE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(letter))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed!"))
                .andExpect(jsonPath("$.details[0]").value("Email should be valid"));
    }
}
