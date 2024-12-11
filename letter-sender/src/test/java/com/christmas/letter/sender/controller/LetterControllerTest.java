package com.christmas.letter.sender.controller;

import com.christmas.letter.sender.helper.LetterUtils;
import com.christmas.letter.sender.model.Letter;
import com.christmas.letter.sender.service.LetterSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessagingException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LetterController.class)
class LetterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LetterSenderService letterSenderService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CREATE_LETTER_API_PATH = "/api/v1/christmas-letters";

    @Test
    void should_returnCreatedStatus_ifValidRequest() throws Exception {
        // Arrange
        Letter christmasLetter = LetterUtils.generateLetter(LetterUtils.generateAddress());

        // Act & Assert
        mockMvc.perform(post(CREATE_LETTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(christmasLetter)))
                .andExpect(status().isCreated());
    }

    @Test
    void should_returnInternalErrorServer_ifMessagingFails() throws Exception {
        // Arrange
        Letter christmasLetter = LetterUtils.generateLetter(LetterUtils.generateAddress());
        doThrow(MessagingException.class).when(letterSenderService).sendLetter(christmasLetter);

        // Act & Assert
        mockMvc.perform(post(CREATE_LETTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(christmasLetter)))
                .andExpect(status().isInternalServerError())
                .andExpect((jsonPath("$.message").value("Publishing message to SNS failed!")));
    }

    @ParameterizedTest
    @MethodSource("com.christmas.letter.sender.helper.LetterUtils#provideInvalidLetters")
    void should_returnBadRequest_ifInvalidRequest(Letter letter, String expectedOutput) throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(post(CREATE_LETTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(letter)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed!"))
                .andExpect(jsonPath("$.details[0]").value(expectedOutput));
    }
}
