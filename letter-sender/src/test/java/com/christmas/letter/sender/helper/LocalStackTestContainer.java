package com.christmas.letter.sender.helper;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public class LocalStackTestContainer {
	private static final String LOCAL_STACK_VERSION = "localstack/localstack:3.4.0";
	private static final String CONTAINER_PATH = "/etc/localstack/init/ready.d/init-resources.sh";

	@Container
	static LocalStackContainer localStackContainer =
			new LocalStackContainer(DockerImageName.parse(LOCAL_STACK_VERSION))
					.withCopyFileToContainer(
							MountableFile.forClasspathResource("init-resources.sh", 484),
							CONTAINER_PATH)
					.withServices(LocalStackContainer.Service.SNS)
					.waitingFor(Wait.forLogMessage(".*Successfully initialized resources.*", 1));

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
		registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);

		registry.add("spring.cloud.aws.sns.region", localStackContainer::getRegion);
		registry.add("spring.cloud.aws.sns.endpoint", localStackContainer::getEndpoint);
	}
}
