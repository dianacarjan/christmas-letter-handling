package com.christmas.letter.processor.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.christmas.letter.processor.repository.LetterRepository;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@EnableDynamoDBRepositories(
		basePackages = "com.christmas.letter.processor.repository",
		includeFilters = {
			@ComponentScan.Filter(
					type = FilterType.ASSIGNABLE_TYPE,
					classes = {LetterRepository.class})
		})
public class DynamoDBConfig {
	@Value("${spring.cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${spring.cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Value("${spring.cloud.aws.dynamodb.region}")
	private String region;

	@Value("${spring.cloud.aws.dynamodb.endpoint}")
	private String endpoint;

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		return AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(
						new AwsClientBuilder.EndpointConfiguration(endpoint, region))
				.withCredentials(awsDynamoDBCredentials())
				.build();
	}

	private AWSCredentialsProvider awsDynamoDBCredentials() {
		return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
	}
}
