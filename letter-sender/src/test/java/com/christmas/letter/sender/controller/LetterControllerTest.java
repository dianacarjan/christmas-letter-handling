package com.christmas.letter.sender.controller;

import com.christmas.letter.sender.model.Letter;
import com.christmas.letter.sender.service.LetterSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessagingException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

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

    @Test
    void should_returnCreatedStatus_ifValidRequest() throws Exception {
        // Arrange
        Letter christmasLetter = new Letter("siobhan@example.com", "Siobhan", "books", "Munich");

        // Act & Assert
        mockMvc.perform(post("/api/v1/christmas-letters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(christmasLetter)))
                .andExpect(status().isCreated());
    }

    @Test
    void should_returnInternalErrorServer_ifMessagingFails() throws Exception {
        // Arrange
        Letter christmasLetter = new Letter("siobhan@example.com", "Siobhan", "candy", "Bruges");
        doThrow(MessagingException.class).when(letterSenderService).sendLetter(christmasLetter);

        // Act & Assert
        mockMvc.perform(post("/api/v1/christmas-letters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(christmasLetter)))
                .andExpect(status().isInternalServerError())
                .andExpect((jsonPath("$.message").value("Publishing message to SNS failed!")));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLetters")
    void should_returnBadRequest_ifInvalidRequest(Letter letter) throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(post("/api/v1/christmas-letters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(letter)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed!"));
    }

    private static Stream<Arguments> provideInvalidLetters() {
        return Stream.of(
                Arguments.of(new Letter("23", "Jane", "Some books", "Berlin")),
                Arguments.of(new Letter("siobhan@example.com", "Siobhan", null, "Munich")),
                Arguments.of(new Letter("siobhan@example.com", "Siobhan", "More books", ""))
        );
    }
}
