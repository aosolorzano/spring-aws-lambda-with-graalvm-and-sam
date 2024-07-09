package hiperium.city.data.function.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public abstract class TestContainersBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestContainersBase.class);

    private static final LocalStackContainer LOCALSTACK_CONTAINER;

    // Singleton containers.
    // See: https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
    static {
        LOCALSTACK_CONTAINER = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.5"))
            .withServices(LocalStackContainer.Service.DYNAMODB)
            .withCopyToContainer(MountableFile.forClasspathResource("localstack/table-setup.sh"),
                "/etc/localstack/init/ready.d/table-setup.sh")
            .withCopyToContainer(MountableFile.forClasspathResource("localstack/table-data.json"),
                "/var/lib/localstack/table-data.json")
            .withLogConsumer(outputFrame -> LOGGER.info(outputFrame.getUtf8String()))
            .withEnv("DEBUG", "0")
            .withEnv("LS_LOG", "info")
            .withEnv("EAGER_SERVICE_LOADING", "1");

        LOCALSTACK_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("aws.region", LOCALSTACK_CONTAINER::getRegion);
        registry.add("aws.accessKeyId", LOCALSTACK_CONTAINER::getAccessKey);
        registry.add("aws.secretAccessKey", LOCALSTACK_CONTAINER::getSecretKey);
        registry.add("spring.cloud.aws.endpoint", () -> LOCALSTACK_CONTAINER.getEndpoint().toString());
    }
}
