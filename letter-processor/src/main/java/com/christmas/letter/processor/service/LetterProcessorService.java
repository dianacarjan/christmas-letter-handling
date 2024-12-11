package com.christmas.letter.processor.service;

import com.christmas.letter.processor.util.LetterConverter;
import com.christmas.letter.processor.dto.LetterMessage;
import com.christmas.letter.processor.exception.NotFoundException;
import com.christmas.letter.processor.model.Letter;
import com.christmas.letter.processor.repository.LetterRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class LetterProcessorService {

    private final LetterRepository letterRepository;

    @SqsListener("${letter-processor.aws.sqs.queue-url}")
    public void listen(@Valid @Payload LetterMessage letterMessage) {
        log.info("Letter Message received {}, on the listen method at {}", letterMessage, OffsetDateTime.now());
        letterRepository.save(LetterConverter.toModel(letterMessage));
        log.info("Letter saved successfully!");
    }

    public Page<Letter> getAll(Pageable pageable) {
        return letterRepository.findAll(pageable);
    }

    public Letter getLetterByEmail(String email) {
        return letterRepository.findById(email)
                .orElseThrow(() -> new NotFoundException(String.format("Letter of email %s not found!", email)));
    }
}
