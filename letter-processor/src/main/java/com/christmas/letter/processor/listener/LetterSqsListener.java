package com.christmas.letter.processor.listener;

import com.christmas.letter.processor.dto.LetterMessage;
import com.christmas.letter.processor.repository.LetterRepository;
import com.christmas.letter.processor.util.LetterConverter;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class LetterSqsListener {

    private final LetterRepository letterRepository;

    @SqsListener("${letter-processor.aws.sqs.queue-url}")
    public void listen(@Valid @Payload LetterMessage letterMessage) {
        log.info("Letter Message received {}, on the listen method at {}", letterMessage, OffsetDateTime.now());
        letterRepository.save(LetterConverter.toModel(letterMessage));
        log.info("Letter saved successfully!");
    }
}
