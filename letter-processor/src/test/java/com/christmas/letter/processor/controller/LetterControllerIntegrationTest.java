package com.christmas.letter.processor.controller;

import com.christmas.letter.processor.helper.LetterUtils;
import com.christmas.letter.processor.helper.LocalStackTestContainer;
import com.christmas.letter.processor.model.Letter;
import com.christmas.letter.processor.repository.LetterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("provideLetters")
    @WithMockUser(roles = {"SANTA"})
    void givenPage_whenGetMethod_thenReturnPageLetters(Pageable pageable, List<Letter> letters) throws Exception {
        // Arrange
        letterRepository.saveAll(letters);

        // Act && Assert
        mockMvc.perform(get(LETTER_API_PATH)
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(letters.size()))
                .andExpect(jsonPath("$.pageSize").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.pageNumber").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.offset").value(pageable.getOffset()));
    }

    @Test
    @WithMockUser(roles = {"SANTA"})
    void givenUnsavedEmail_whenGetByEmail_thenThrowNotFoundException() throws Exception {
        // Arrange
        String email = "test@example.com";

        // Act && Assert
        mockMvc.perform(get(String.format("%s/{email}", LETTER_API_PATH), email))
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format("Letter of email %s not found!", email)));
    }

    @Test
    @WithMockUser(roles = {"SANTA"})
    void givenInvalidEmail_whenGetByEmail_thenThrowValidationException() throws Exception {
        // Arrange
        String email = "invalid_email";

        // Act && Assert
        mockMvc.perform(get(String.format("%s/{email}", LETTER_API_PATH), email))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failure"))
                .andExpect(jsonPath("$.errors[0].email").value("Invalid email"));
    }

    private static Stream<Arguments> provideLetters() {
        return Stream.of(
                Arguments.of(PageRequest.of(0, 10), getNewLetters(1)),
                Arguments.of(PageRequest.of(1, 2), getNewLetters(3))
        );
    }

    private static List<Letter> getNewLetters(int count) {
        return IntStream.range(0, count).mapToObj(el -> LetterUtils.generateLetter()).toList();
    }
}
