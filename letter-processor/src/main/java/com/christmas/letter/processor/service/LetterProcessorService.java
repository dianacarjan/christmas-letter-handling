package com.christmas.letter.processor.service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@Service
public class LetterProcessorService {
    @SqsListener("${letter-processor.aws.sqs.queue-url}")
    public void listen(Message<?> message) {
        log.info("Message received {}, on the listen method at {}", message.getPayload(), OffsetDateTime.now());
    }
}
