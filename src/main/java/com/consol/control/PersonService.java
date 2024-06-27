package com.consol.control;

import com.consol.entity.IdTO;
import com.consol.entity.PersonTO;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;
    private final Randomizer randomizer;
    private final MeterRegistry meterRegistry;

    public PersonService(
            final DynamoDbClient dynamoDbClient,
            @Value("${dynamodb.tablename}") final String tableName,
            final Randomizer randomizer,
            final MeterRegistry meterRegistry
    ) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        this.randomizer = randomizer;
        this.meterRegistry = meterRegistry;
    }

    public IdTO store(final PersonTO personTO) {
        final String uuid = UUID.randomUUID().toString();
        LOGGER.info("Storing person with ID: {}", uuid);
        dynamoDbClient.putItem(
                PutItemRequest.builder()
                        .tableName(tableName)
                        .item(
                                Map.of(
                                        "id", AttributeValue.builder().s(uuid).build(),
                                        "firstname", AttributeValue.builder().s(personTO.getFirstname()).build(),
                                        "lastname", AttributeValue.builder().s(personTO.getLastname()).build(),
                                        "age", AttributeValue.builder().n(String.valueOf(personTO.getAge())).build()
                                )
                        )
                        .build()
        );
        return new IdTO(uuid);
    }

    public PersonTO getPersonById(final String uuid) {
        LOGGER.info("Fetching person for ID: {}", uuid);
        final Map<String, AttributeValue> values = dynamoDbClient.getItem(
                GetItemRequest.builder()
                        .tableName(tableName)
                        .key(Map.of("id", AttributeValue.builder().s(uuid).build()))
                        .build()
        ).item();
        return new PersonTO(
                values.get("firstname").s(),
                values.get("lastname").s(),
                Integer.parseInt(values.get("age").n())
        );
    }

    public PersonTO getRandomPerson() {
        LOGGER.info("Fetching a random persons");
        return new PersonTO(randomizer.getRandomFirstName(), randomizer.getRandomLastName(), randomizer.getRandomAge());
    }

    public Optional<PersonTO> maybeFail() {
        LOGGER.info("Maybe fail when fetching a random person");
        meterRegistry.counter("maybe.fail.all").increment();
        if (randomizer.getRandomAge() % 2 == 0) {
            LOGGER.info("Got a person. Ok!");
            return Optional.of(getRandomPerson());
        } else {
            meterRegistry.counter("maybe.fail.fail").increment();
            LOGGER.error("Did not get a person. Fail!");
            return Optional.empty();
        }
    }
}
