package com.github.byrage.application.context;

import com.github.byrage.modules.localstack.support.LocalStackConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
class LocalContextLoadTest {

    @Autowired
    private LocalStackConfiguration localStackConfiguration;

    @Test
    void contextLoad() {
        assertThat(localStackConfiguration).isNotNull();
        assertThat(localStackConfiguration.isInitFinished()).isTrue();
    }
}