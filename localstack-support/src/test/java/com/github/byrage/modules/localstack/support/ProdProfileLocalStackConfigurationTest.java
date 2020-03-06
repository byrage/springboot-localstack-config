package com.github.byrage.modules.localstack.support;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ActiveProfiles("prod")
@SpringBootTest
class ProdProfileLocalStackConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("prod 환경에서 로컬스택 설정과 AWS Client 빈이 주입되지 않음을 검증")
    void prod() {
        assertThat(catchThrowable(() -> applicationContext.getBean(LocalStackConfiguration.class)))
                .isInstanceOf(NoSuchBeanDefinitionException.class);
        assertThat(catchThrowable(() -> applicationContext.getBean(SqsAsyncClient.class)))
                .isInstanceOf(NoSuchBeanDefinitionException.class);
        assertThat(catchThrowable(() -> applicationContext.getBean(AmazonDynamoDBAsync.class)))
                .isInstanceOf(NoSuchBeanDefinitionException.class);
        assertThat(catchThrowable(() -> applicationContext.getBean(DynamoDbAsyncClient.class)))
                .isInstanceOf(NoSuchBeanDefinitionException.class);
    }
}