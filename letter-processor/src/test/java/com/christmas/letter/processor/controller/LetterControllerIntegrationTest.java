package com.christmas.letter.processor.controller;

import com.christmas.letter.processor.helper.LocalStackTestContainer;
import com.christmas.letter.processor.repository.LetterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:config-test.properties")
class LetterControllerIntegrationTest extends LocalStackTestContainer {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LetterRepository letterRepository;

    private static final String LETTER_API_PATH = "/api/v1/christmas-letters";

    @BeforeEach
    void setup() {
        letterRepository.deleteAll();
    }

    @Test
    void givenUnsavedEmail_whenGetByEmail_thenThrowNotFoundException() throws Exception {
        // Arrange
        String email = "test@example.com";

        // Act && Assert
        mockMvc.perform(get(String.format("%s/{email}", LETTER_API_PATH), email))
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format("Letter of email %s not found!", email)));
    }

    @Test
    void givenInvalidEmail_whenGetByEmail_thenThrowValidationException() throws Exception {
        // Arrange
        String email = "invalid_email";

        // Act && Assert
        mockMvc.perform(get(String.format("%s/{email}", LETTER_API_PATH), email))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failure"))
                .andExpect(jsonPath("$.errors[0].email").value("Invalid email"));
    }
}
