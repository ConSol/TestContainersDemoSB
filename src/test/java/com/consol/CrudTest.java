package com.consol;

import com.consol.entity.PersonTO;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.clickhouse.ClickHouseContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
class CrudTest {

    /*
        It is necessary to start this test with following ENV variables

        JAVA_TOOL_OPTIONS="-javaagent:opentelemetry-javaagent.jar"
        OTEL_SERVICE_NAME="Testcontainers"
        OTEL_JAVAAGENT_CONFIGURATION_FILE="agent.properties"
     */

    static Network network = Network.newNetwork();

    @Container
    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
                    .withCopyFileToContainer(
                            MountableFile.forHostPath("src/test/resources/init-resources.sh"),
                            "/etc/localstack/init/ready.d/init-resources.sh"
                    )
                    .withServices(LocalStackContainer.Service.DYNAMODB)
                    .withNetwork(network);

    @Container
    static ClickHouseContainer clickHouseContainer =
            new ClickHouseContainer(DockerImageName.parse("clickhouse/clickhouse-server"))
                    .withNetwork(network)
                    .withDatabaseName("otel")
                    .withUsername("test")
                    .withPassword("test")
                    .withExposedPorts(8123)
                    .withNetworkAliases("clickhouse")
                    .withCreateContainerCmdModifier(cmd -> cmd
                            .withPortBindings(new PortBinding(Ports.Binding.bindPort(8123), new ExposedPort(8123)))
                    );

    @Container
    static GenericContainer<?> otelCollector =
            new GenericContainer<>(DockerImageName.parse("otel/opentelemetry-collector-contrib:latest-arm64"))
                    .withCopyToContainer(
                            MountableFile.forHostPath("src/test/resources/config.yaml"),
                            "/etc/otelcol-contrib/config.yaml"
                    )
                    .withNetwork(network)
                    .dependsOn(clickHouseContainer)
                    .withExposedPorts(4317)
                    .withExposedPorts(4318)
                    .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig()
                            .withPortBindings(List.of(
                                    new PortBinding(Ports.Binding.bindPort(4317), new ExposedPort(4317)),
                                    new PortBinding(Ports.Binding.bindPort(4318), new ExposedPort(4318))
                            ))
                    );

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("aws.accessKeyId", () -> localStack.getAccessKey());
        dynamicPropertyRegistry.add("aws.secretKeyId", () -> localStack.getSecretKey());
        dynamicPropertyRegistry.add("dynamodb.endpoint", () -> localStack.getEndpoint());
        dynamicPropertyRegistry.add("dynamodb.tablename", () -> "test");
        dynamicPropertyRegistry.add("spring.datasource.url", () -> "jdbc:ch://localhost:8123/otel");
        dynamicPropertyRegistry.add("spring.datasource.username", () -> "test");
        dynamicPropertyRegistry.add("spring.datasource.password", () -> "test");
        dynamicPropertyRegistry.add("spring.datasource.driver-class-name", () -> "com.clickhouse.jdbc.ClickHouseDriver");
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void postAndGet() throws InterruptedException {

        final PersonTO personIn = new PersonTO("John", "Doe", 30);

        final String id = given()
                .contentType(ContentType.JSON)
                .body(personIn)
                .when()
                .post("/person")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        final PersonTO personOut = given()
                .when()
                .get("/person/" + id)
                .then()
                .statusCode(200)
                .extract()
                .as(PersonTO.class);

        assert personIn.equals(personOut);

        Thread.sleep(10000);

        final List<Long> result = jdbcTemplate.query("select count(*) from otel_logs where Body like '%Storing person%'", (rs, rowNum) -> rs.getLong(1));
        assert result.size() == 1;
        assert result.get(0) > 0;

        //Thread.sleep(1000000);
    }
}
