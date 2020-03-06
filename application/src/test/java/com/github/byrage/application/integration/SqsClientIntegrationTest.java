package com.github.byrage.application.integration;

import com.github.byrage.application.verifier.SqsVerifier;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.Collection;

@ActiveProfiles("local")
@SpringBootTest
public class SqsClientIntegrationTest {

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @TestFactory
    Collection<DynamicTest> verifySqs() {
        return SqsVerifier.verifySqs(sqsAsyncClient);
    }
}
