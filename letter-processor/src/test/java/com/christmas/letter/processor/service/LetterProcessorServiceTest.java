package com.christmas.letter.processor.service;

import com.christmas.letter.processor.dto.CachedPage;
import com.christmas.letter.processor.exception.NotFoundException;
import com.christmas.letter.processor.helper.LetterUtils;
import com.christmas.letter.processor.model.Letter;
import com.christmas.letter.processor.repository.LetterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class LetterProcessorServiceTest {

    @Mock
    private LetterRepository letterRepository;

    @InjectMocks
    private LetterProcessorService letterProcessorService;

    @Test()
    void givenRequest_whenGetAll_thenReturnSavedLetters() {
        // Arrange
        List<Letter> savedLetters = List.of(LetterUtils.generateLetter());
        Page<Letter> savedPage = new PageImpl<>(savedLetters);
        Pageable pageable = PageRequest.of(0, 10);
        when(letterRepository.findAll(pageable)).thenReturn(savedPage);

        // Act
        CachedPage<Letter> result = letterProcessorService.getAll(pageable);

        // Assert
        assertThat(result.getTotalElements()).isOne();
    }

    @Test()
    void givenEmail_whenGetLetterByEmail_thenReturnCorrectLetter() {
        // Arrange
        String email = "shibby@example.com";
        Letter savedLetter = LetterUtils.generateLetter();
        when(letterRepository.findById(email)).thenReturn(Optional.of(savedLetter));

        // Act
        Letter result = letterProcessorService.getLetterByEmail(email);

        // Assert
        assertThat(result).isEqualTo(savedLetter);
    }

    @Test()
    void givenNonExistentEmail_whenGetLetterByEmail_thenThrowException() {
        // Arrange
        String email = "test@example.com";
        when(letterRepository.findById(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> letterProcessorService.getLetterByEmail(email))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("Letter of email %s not found!", email));
    }
}
