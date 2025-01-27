package com.christmas.letter.sender.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = SnsConfigProperties.class)
@EnableConfigurationProperties(SnsConfigProperties.class)
@TestPropertySource("classpath:config-test.properties")
class SnsConfigPropertiesTest {
	@Autowired private SnsConfigProperties snsConfigProperties;

	@Test
	void givenProperty_whenBinding_thenTopicArnIsSet() {
		assertThat(snsConfigProperties.getTopicArn())
				.isEqualTo("arn:aws:sns:us-east-1:000000000000:test-topic");
	}
}
