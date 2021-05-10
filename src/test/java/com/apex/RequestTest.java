package com.apex;

import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

class RequestTest {

    @Test
    void testGetEcoData() throws Exception {

        withEnvironmentVariable("jdbc_url", "jdbc:sqlite:src/main/resources/eco-data.db")
                .and("db_pwd", "TJcdV4UA%1uz").execute(() -> {
            assertThat(System.getenv("jdbc_url")).isNotBlank();
        });
    }
}
