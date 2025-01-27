package com.christmas.letter.processor.config;

import com.christmas.letter.processor.util.SqsMessageConverter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Slf4j
@Configuration
public class SqsConfig {

	@Bean
	public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(
			SqsAsyncClient sqsAsyncClient, ObjectMapper objectMapper) {

		return SqsMessageListenerContainerFactory.builder()
				.sqsAsyncClient(sqsAsyncClient)
				.configure(
						options -> options.messageConverter(new SqsMessageConverter(objectMapper)))
				.build();
	}

	@Bean
	public ObjectMapper customObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return objectMapper;
	}
}
