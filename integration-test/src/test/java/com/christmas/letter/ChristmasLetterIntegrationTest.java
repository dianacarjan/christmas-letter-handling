package com.christmas.letter;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class ChristmasLetterIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private static final LocalStackContainer localStackContainer;

    // as configured in initializing hook script 'init-resources.sh' in src/test/resources
    private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:000000000000:christmas-letter-creation";
    private static final String QUEUE_URL = "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/christmas-letter-processing";

    static {
        localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.4.0"))
                .withCopyFileToContainer(MountableFile.forClasspathResource("init-resources.sh", 484), "/etc/localstack/init/ready.d/init-resources.sh")
                .withServices(LocalStackContainer.Service.SNS, LocalStackContainer.Service.SQS)
                .waitingFor(Wait.forLogMessage(".*Successfully initialized resources.*", 1));
        localStackContainer.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);

        registry.add("spring.cloud.aws.sns.region", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.sns.endpoint", localStackContainer::getEndpoint);
        registry.add("letter-sender.aws.sns.topic-arn", () -> TOPIC_ARN);

        registry.add("spring.cloud.aws.sqs.region", localStackContainer::getRegion);
        registry.add("spring.cloud.aws.sqs.endpoint", localStackContainer::getEndpoint);
        registry.add("letter-processor.aws.sqs.queue-url", () -> QUEUE_URL);
    }

    @Test
    void test(CapturedOutput output) throws Exception {
        // Arrange
        String email = "siobhan@example.com";
        String name = RandomString.make();
        String wishes = RandomString.make();
        String location = RandomString.make();
        String letterCreationRequestBody = String.format("""
                {
                	"email"   : "%s",
                	"name"  : "%s",
                	"wishes" : "%s",
                	"location" : "%s"
                }
                """, email, name, wishes, location);


        String letterCreationApiPath = "/api/v1/christmas-letters";

        // Act
        mockMvc.perform(post(letterCreationApiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(letterCreationRequestBody))
                .andExpect(status().isCreated());

        // Assert
        String expectedPublisherLog = String.format("Successfully published message to topic ARN: %s", TOPIC_ARN);
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains(expectedPublisherLog));

        String expectedSubscriberLog = "Message received";
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> output.getAll().contains(expectedSubscriberLog));
    }
}
