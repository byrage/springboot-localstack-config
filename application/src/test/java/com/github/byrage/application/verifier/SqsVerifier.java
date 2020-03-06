package com.github.byrage.application.verifier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DynamicTest;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqsVerifier {

    public static Collection<DynamicTest> verifySqs(SqsAsyncClient sqsAsyncClient) {
        String queueName = "test-queue";

        return Arrays.asList(
                DynamicTest.dynamicTest("SQS 초기화 검증", () ->
                        assertThat(sqsAsyncClient.listQueues())
                                .satisfies(listQueuesResponseCompletableFuture -> {
                                    ListQueuesResponse response = listQueuesResponseCompletableFuture.join();
                                    assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
                                })
                                .isDone()),

                DynamicTest.dynamicTest("큐 생성", () -> {
                    CreateQueueRequest request = CreateQueueRequest.builder().queueName(queueName).build();
                    assertThat(sqsAsyncClient.createQueue(request))
                            .satisfies(createQueueResponseCompletableFuture -> {
                                CreateQueueResponse response = createQueueResponseCompletableFuture.join();
                                assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
                                assertThat(response.queueUrl()).endsWith(queueName);
                            })
                            .isDone();

                    assertThat(sqsAsyncClient.listQueues())
                            .satisfies(listQueuesResponseCompletableFuture -> {
                                ListQueuesResponse response = listQueuesResponseCompletableFuture.join();
                                assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
                                assertThat(response.queueUrls()).isNotEmpty();
                                assertThat(response.queueUrls()).hasOnlyOneElementSatisfying(queueUrl ->
                                        assertThat(queueUrl).endsWith(queueName));
                            })
                            .isDone();
                }),

                DynamicTest.dynamicTest("메세지 전송", () -> {
                    String queueUrl = sqsAsyncClient.listQueues()
                            .join()
                            .queueUrls()
                            .get(0);
                    String messageBody = "message test";

                    SendMessageRequest sendMessageRequest = SendMessageRequest.builder().queueUrl(queueUrl).messageBody(messageBody).build();
                    assertThat(sqsAsyncClient.sendMessage(sendMessageRequest))
                            .satisfies(sendMessageResponseCompletableFuture -> {
                                SendMessageResponse response = sendMessageResponseCompletableFuture.join();
                                assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
                                assertThat(response.messageId()).isNotEmpty();
                                assertThat(response.md5OfMessageBody()).isNotEmpty();
                            })
                            .isDone();
                }),

                DynamicTest.dynamicTest("메세지 수신", () -> {
                    String queueUrl = sqsAsyncClient.listQueues()
                            .join()
                            .queueUrls()
                            .get(0);
                    String messageBody = "message test";

                    ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .build();
                    assertThat(sqsAsyncClient.receiveMessage(receiveMessageRequest))
                            .satisfies(receiveMessageResponseCompletableFuture -> {
                                ReceiveMessageResponse response = receiveMessageResponseCompletableFuture.join();
                                assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
                                assertThat(response.messages()).extracting(Message::body).containsOnlyOnce(messageBody);
                                assertThat(response.messages()).extracting(Message::messageId).isNotEmpty();
                                assertThat(response.messages()).extracting(Message::receiptHandle).isNotEmpty();
                            })
                            .isDone();
                }),

                DynamicTest.dynamicTest("배치 메세지 전송", () -> {
                    String queueUrl = sqsAsyncClient.listQueues()
                            .join()
                            .queueUrls()
                            .get(0);
                    SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder().queueUrl(queueUrl)
                            .entries(SendMessageBatchRequestEntry.builder()
                                            .id("111")
                                            .messageBody("message1")
                                            .build(),
                                    SendMessageBatchRequestEntry.builder()
                                            .id("222")
                                            .messageBody("message2")
                                            .build(),
                                    SendMessageBatchRequestEntry.builder()
                                            .id("333")
                                            .messageBody("message3")
                                            .build())
                            .build();

                    assertThat(sqsAsyncClient.sendMessageBatch(sendMessageBatchRequest))
                            .satisfies(sendMessageBatchResponseCompletableFuture -> {
                                SendMessageBatchResponse response = sendMessageBatchResponseCompletableFuture.join();
                                assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
                                assertThat(response.successful()).extracting(SendMessageBatchResultEntry::id).containsExactlyInAnyOrder("111", "222", "333");
                            })
                            .isDone();
                }),

                DynamicTest.dynamicTest("퍼지", () -> {
                    String queueUrl = sqsAsyncClient.listQueues()
                            .join()
                            .queueUrls()
                            .get(0);
                    PurgeQueueRequest purgeQueueRequest = PurgeQueueRequest.builder().queueUrl(queueUrl).build();
                    assertThat(sqsAsyncClient.purgeQueue(purgeQueueRequest))
                            .satisfies(purgeQueueResponseCompletableFuture -> {
                                PurgeQueueResponse response = purgeQueueResponseCompletableFuture.join();
                                assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
                            })
                            .isDone();

                    ReceiveMessageRequest receiveMessageRequest2 = ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .build();
                    assertThat(sqsAsyncClient.receiveMessage(receiveMessageRequest2))
                            .satisfies(receiveMessageResponseCompletableFuture -> {
                                ReceiveMessageResponse response = receiveMessageResponseCompletableFuture.join();
                                assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
                                assertThat(response.messages()).isEmpty();
                            })
                            .isDone();
                }));
    }
}
