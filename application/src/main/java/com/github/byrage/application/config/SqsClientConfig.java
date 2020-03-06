package com.github.byrage.application.config;

import org.springframework.context.annotation.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Profile("!local")
@Configuration
public class SqsClientConfig {

    @Bean
    @Primary
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();
    }
}