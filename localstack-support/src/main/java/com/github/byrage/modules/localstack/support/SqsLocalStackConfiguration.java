package com.github.byrage.modules.localstack.support;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;
import java.time.Duration;

@Profile("local")
@Configuration
@ConditionalOnClass({SqsAsyncClient.class})
@DependsOn(LocalStackConfiguration.BEAN_NAME)
public class SqsLocalStackConfiguration {

    @Bean
    public SqsAsyncClient localstackSqsReactiveClient() {
        return SqsAsyncClient.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create("x", "x"))
                .region(Region.AP_NORTHEAST_2)
                .endpointOverride(URI.create(LocalStackConfiguration.getEndpointConfiguration(LocalStackContainer.Service.SQS).getServiceEndpoint()))
                .overrideConfiguration(builder -> builder
                        .apiCallAttemptTimeout(Duration.ofMillis(7000))
                        .apiCallTimeout(Duration.ofMillis(7000))
                )
                .build();
    }
}
