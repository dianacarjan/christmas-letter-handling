package com.christmas.letter.processor.util;

import com.christmas.letter.processor.exception.LetterDeserializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.model.Message;

@Slf4j
@RequiredArgsConstructor
public class SqsMessageConverter extends SqsMessagingMessageConverter {

    private final ObjectMapper objectMapper;

    @Override
    protected Object getPayloadToDeserialize(Message message) {
        String body = message.body();
        log.info("Deserializing SQS message {} ", body);

        return unwrap(body);
    }

    private String unwrap(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);

            return node.path("Message")
                    .asText();
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
            throw new LetterDeserializationException("Invalid payload");
        }
    }
}
