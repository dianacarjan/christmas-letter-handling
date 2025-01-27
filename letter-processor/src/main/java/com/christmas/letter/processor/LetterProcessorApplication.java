package com.christmas.letter.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableAutoConfiguration(
		exclude = {RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class})
public class LetterProcessorApplication {

	public static void main(String... args) {
		SpringApplication.run(LetterProcessorApplication.class, args);
	}
}
