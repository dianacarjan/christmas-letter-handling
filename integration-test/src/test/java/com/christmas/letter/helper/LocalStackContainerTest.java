package com.christmas.letter.helper;


import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@Testcontainers
public class LocalStackContainerTest {

    private static final String LOCAL_STACK_VERSION = "localstack/localstack:3.4.0";

    @Container
    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse(LOCAL_STACK_VERSION))
            .withCopyFileToContainer(MountableFile.forClasspathResource("init-resources.sh", 484), "/etc/localstack/init/ready.d/init-resources.sh")
            .withServices(LocalStackContainer.Service.SNS, SQS)
            .waitingFor(Wait.forLogMessage(".*Successfully initialized resources.*", 1));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);

        registry.add("spring.cloud.aws.sns.region", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.sns.endpoint", localStackContainer::getEndpoint);

        registry.add("spring.cloud.aws.sqs.region", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.sqs.endpoint", localStackContainer::getEndpoint);
    }
}
