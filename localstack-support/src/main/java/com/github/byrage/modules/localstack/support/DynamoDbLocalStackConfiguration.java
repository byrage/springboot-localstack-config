package com.github.byrage.modules.localstack.support;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Profile("local")
@Configuration
@ConditionalOnClass({AmazonDynamoDBAsync.class, DynamoDbAsyncClient.class})
@DependsOn(LocalStackConfiguration.BEAN_NAME)
public class DynamoDbLocalStackConfiguration {

    @Bean
    public AmazonDynamoDBAsync localstackAmazonDynamoDBAsync() {
        ClientConfiguration clientConfiguration = createClientConfiguration();

        return AmazonDynamoDBAsyncClientBuilder.standard()
                .withClientConfiguration(clientConfiguration)
                .withCredentials(LocalStackConfiguration.getCredentials())
                .withEndpointConfiguration(LocalStackConfiguration.getEndpointConfiguration(LocalStackContainer.Service.DYNAMODB))
                .build();
    }

    @Bean
    public DynamoDbAsyncClient localstackDynamoDbAsyncClient() {
        return DynamoDbAsyncClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(LocalStackConfiguration.getCredentialsProvider())
                .endpointOverride(URI.create(LocalStackConfiguration.getEndpointConfiguration(LocalStackContainer.Service.DYNAMODB).getServiceEndpoint()))
                .build();
    }

    private ClientConfiguration createClientConfiguration() {
        return new ClientConfiguration()
                .withMaxErrorRetry(3)
                .withClientExecutionTimeout(4000)
                .withConnectionTimeout(1000)
                .withGzip(true)
                .withMaxConnections(1000)
                .withThrottledRetries(true)
                .withSocketTimeout(1000)
                .withRequestTimeout(1000);
    }
}
