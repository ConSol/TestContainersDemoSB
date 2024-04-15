package com.consol;

import com.consol.entity.PersonTO;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
class CrudTest {

    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
                    .withCopyFileToContainer(
                            MountableFile.forHostPath("src/test/resources/init-resources.sh"),
                            "/etc/localstack/init/ready.d/init-resources.sh"
                    );

    @BeforeAll
    static void init() {
        localStack.start();
    }

    @AfterAll
    static void stop() {
        localStack.stop();
    }

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("aws.accessKeyId", () -> localStack.getAccessKey());
        dynamicPropertyRegistry.add("aws.secretKeyId", () -> localStack.getSecretKey());
        dynamicPropertyRegistry.add("dynamodb.endpoint", () -> localStack.getEndpoint());
        dynamicPropertyRegistry.add("dynamodb.tablename", () -> "test");
    }

    @Test
    void postAndGet() {

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
    }
}
