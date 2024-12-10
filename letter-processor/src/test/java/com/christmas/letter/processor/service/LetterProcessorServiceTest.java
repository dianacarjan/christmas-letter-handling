package com.christmas.letter.processor.service;

import com.christmas.letter.processor.configuration.LetterProcessorTestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class, SpringExtension.class})
@Import(LetterProcessorTestConfiguration.class)
class LetterProcessorServiceTest {

    @Autowired
    private LetterProcessorService letterProcessorService;

    @Test()
    void givenPolling_whenListen_thenMessageReceived(CapturedOutput output) {
        // Arrange & Act
        letterProcessorService.listen(new GenericMessage<>("Test payload", Map.of("key","value" )));

        // Assert
        assertThat(output.getAll()).contains("Message received Test payload, on the listen method");
    }
}
