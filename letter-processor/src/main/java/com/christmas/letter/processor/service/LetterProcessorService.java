package com.christmas.letter.processor.service;

import com.christmas.letter.processor.exception.NotFoundException;
import com.christmas.letter.processor.model.Letter;
import com.christmas.letter.processor.repository.LetterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LetterProcessorService {

    private final LetterRepository letterRepository;

    public Page<Letter> getAll(Pageable pageable) {
        return letterRepository.findAll(pageable);
    }

    public Letter getLetterByEmail(String email) {
        return letterRepository.findById(email)
                .orElseThrow(() -> new NotFoundException(String.format("Letter of email %s not found!", email)));
    }
}
