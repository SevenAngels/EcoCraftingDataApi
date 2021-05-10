package com.apex;

import com.apex.response.ApiGatewayResponse;
import com.apex.response.EcoDataResponse;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

class RequestTests {

    private static final Logger LOGGER = Logger.getLogger(RequestTests.class);

    @Test
    void testGetEcoData() throws Exception {

        withEnvironmentVariable("jdbc_url", "jdbc:sqlite:src/main/resources/eco-data.db")
                .and("db_pwd", "TJcdV4UA%1uz").execute(() -> {
                    ApiGatewayResponse response = new DataRequestHandler().handleRequest(null, null);

                    assertThat(response).isNotNull();
                    assertThat(response.getStatusCode()).isEqualTo(200);
                    assertThat(response.getHeaders()).containsEntry("Content-Type", "application/json");

                    String responseBody = response.getBody();
                    assertThat(responseBody).isNotBlank();

                    LOGGER.info("Response body length: " + responseBody.length());

                    EcoDataResponse dataResponse = new Gson().fromJson(responseBody, EcoDataResponse.class);
                    assertThat(dataResponse).isNotNull();
                });
    }
}
