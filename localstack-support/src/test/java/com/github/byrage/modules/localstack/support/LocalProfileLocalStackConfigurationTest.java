package com.github.byrage.modules.localstack.support;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest
class LocalProfileLocalStackConfigurationTest {

    @Autowired
    private LocalStackConfiguration localStackConfiguration;

    @Autowired
    private SqsAsyncClient sqsAsyncClient;
    @Autowired
    private AmazonDynamoDBAsync amazonDynamoDBAsync;
    @Autowired
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @Test
    @DisplayName("local 환경에서 로컬스택 실행중이고, AWS Client 빈이 정상적으로 주입되었는지 검증")
    void localBeta() {
        assertThat(localStackConfiguration).isNotNull();
        assertThat(localStackConfiguration.isInitFinished()).isTrue();

        assertThat(sqsAsyncClient).isNotNull();
        assertThat(amazonDynamoDBAsync).isNotNull();
        assertThat(dynamoDbAsyncClient).isNotNull();
    }
}