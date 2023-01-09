package io.cip.services.cake;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

@CakeServiceSpringTest
class OpenApiTest {

    @Autowired
    private WebTestClient httpClient;


    @Test
    void returnsOpenApiJson() {
        httpClient.get().uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //
                .jsonPath("$.openapi").isEqualTo("3.0.1")
                .jsonPath("$.info.title").isEqualTo("OpenAPI definition");
    }

}
