package com.github.byrage.application.context;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("prod")
class ProdContextLoadTest {

    @Test
    void contextLoad() {
    }
}