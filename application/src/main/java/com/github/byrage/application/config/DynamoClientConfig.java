package com.github.byrage.application.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Profile("!local")
@Configuration
public class DynamoClientConfig {

    @Bean
    @Primary
    public AmazonDynamoDBAsync amazonDynamoDB() {
        return AmazonDynamoDBAsyncClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
    }

    @Bean
    @Primary
    public DynamoDbAsyncClient reactiveAmazonDynamoDB() {
        return DynamoDbAsyncClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();
    }
}
