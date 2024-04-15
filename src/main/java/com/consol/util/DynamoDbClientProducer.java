package com.consol.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Configuration
public class DynamoDbClientProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbClientProducer.class);

    @Bean(destroyMethod = "close")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DynamoDbClient dynamoDbClient(
            @Value("${aws.accessKeyId}") final String accessKey,
            @Value("${aws.secretKeyId}") final String secretKey,
            @Value("${dynamodb.endpoint:#{null}}") final URI endpoint
    ) {
        LOGGER.info("Creating DynamoDB client");
        final DynamoDbClientBuilder dynamoDbClientBuilder = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));

        if (endpoint != null) {
            LOGGER.info("Using endpoint override: {}", endpoint);
            dynamoDbClientBuilder.endpointOverride(endpoint);
        }

        return dynamoDbClientBuilder.build();
    }
}
