package com.christmas.letter.processor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class LetterProcessorServiceTest {

    @InjectMocks
    private LetterProcessorService letterProcessorService;

    @Test()
    void givenPolling_whenListen_thenMessageReceived(CapturedOutput output) {
        // Arrange & Act
        letterProcessorService.listen(new GenericMessage<>("Test payload", Map.of("key","value" )));

        // Assert
        assertThat(output.getAll()).contains("Message received Test payload, on the listen method");
    }
}
