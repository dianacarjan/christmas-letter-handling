package com.christmas.letter.sender.controller;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.christmas.letter.sender.config.TestSecurityConfig;
import com.christmas.letter.sender.helper.LetterUtils;
import com.christmas.letter.sender.helper.LocalStackTestContainer;
import com.christmas.letter.sender.model.Letter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
@Import(TestSecurityConfig.class)
@TestPropertySource("classpath:config-test.properties")
class LetterControllerIntegrationTest extends LocalStackTestContainer {
	@Autowired private MockMvc mockMvc;

	@Autowired private ObjectMapper objectMapper;

	@Value("${letter-sender.aws.sns.topic-arn}")
	String topicArn;

	private static final String LETTER_CREATE_PATH = "/api/v1/christmas-letters";

	@Test
	void givenLetter_whenPublishingToSNS_thenMessageQueueIsReceived(CapturedOutput output)
			throws Exception {
		// Arrange
		Letter christmasLetter = LetterUtils.generateLetter(LetterUtils.generateAddress());

		// Act
		mockMvc.perform(
						post(LETTER_CREATE_PATH)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(christmasLetter)))
				.andExpect(status().isCreated());

		// Assert
		String expectedPublisherLog =
				String.format("Successfully published message to topic ARN: %s", topicArn);
		await().atMost(1, TimeUnit.SECONDS)
				.until(() -> output.getAll().contains(expectedPublisherLog));
	}

	@ParameterizedTest
	@MethodSource("com.christmas.letter.sender.helper.LetterUtils#provideInvalidLetters")
	void givenInvalidLetter_whenSendingLetter_thenTopicIsEmpty(Letter letter, String expectedOutput)
			throws Exception {
		// Arrange & Act & Assert
		mockMvc.perform(
						post(LETTER_CREATE_PATH)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(letter)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Validation failed!"))
				.andExpect(jsonPath("$.details[0]").value(expectedOutput));
	}
}
