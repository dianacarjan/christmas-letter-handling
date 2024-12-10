package com.christmas.letter.processor.configuration;

import com.christmas.letter.processor.service.LetterProcessorService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class LetterProcessorTestConfiguration {
    @Bean
    public LetterProcessorService letterProcessorService() {
        return new LetterProcessorService();
    }
}
