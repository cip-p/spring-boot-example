package io.cip.services.cake.controller;

import io.cip.services.cake.CakeServiceSpringTest;
import io.cip.services.cake.repository.CakeRepository;
import io.cip.services.cake.repository.model.Cake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Map;

import static java.lang.Long.parseLong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@CakeServiceSpringTest
class CakeControllerTest {

    public static final int UNKNOWN_CAKE_ID = 9999999;

    @Autowired
    private WebTestClient httpClient;

    @Autowired
    private CakeRepository cakeRepository;

    @BeforeEach
    void deleteAllCakesFromDatabase() {
        cakeRepository.deleteAllInBatch();
    }


    @Nested
    public class GetCakes {

        @Test
        void returnsNoCakesWhenNoCakesArePresentInDatabase() {

            getCakesRequest()
                    .headers(CakeControllerTest.this::withBasicAuth)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody()
                    .json("{\"cakes\":[]}");
        }

        @Test
        void returnsCakesOrderedByTitleWhenCakesArePresentInDatabase() {

            long id2 = givenTheFollowingCakeExistsInDatabase(aCake().title("some title 2").description(null).build()).getId();
            long id3 = givenTheFollowingCakeExistsInDatabase(aCake().title("some title 3").description("some description 3").build()).getId();
            long id1 = givenTheFollowingCakeExistsInDatabase(aCake().title("some title 1").description("some description 1").build()).getId();

            getCakesRequest()
                    .headers(CakeControllerTest.this::withBasicAuth)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody()
                    //
                    .jsonPath("$.cakes[0].id").isEqualTo(id1)
                    .jsonPath("$.cakes[0].title").isEqualTo("some title 1")
                    .jsonPath("$.cakes[0].description").isEqualTo("some description 1")
                    //
                    .jsonPath("$.cakes[1].id").isEqualTo(id2)
                    .jsonPath("$.cakes[1].title").isEqualTo("some title 2")
                    .jsonPath("$.cakes[1].description").isEmpty()
                    //
                    .jsonPath("$.cakes[2].id").isEqualTo(id3)
                    .jsonPath("$.cakes[2].title").isEqualTo("some title 3")
                    .jsonPath("$.cakes[2].description").isEqualTo("some description 3");
        }

        @Test
        void returnsUnauthorizedWhenAuthorizationHeaderIsMissing() {

            getCakesRequest()
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody().isEmpty();
        }

        @Test
        void returnsUnauthorizedWhenUsernameAndPasswordAreIncorrect() {

            getCakesRequest()
                    .headers(headers -> headers.setBasicAuth("some-incorrect-username", "some-incorrect-password"))
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody().isEmpty();
        }

        private WebTestClient.RequestHeadersSpec<?> getCakesRequest() {
            return httpClient.get().uri("/cakes");
        }
    }

    @Nested
    public class GetCakeById {

        @Test
        void getCakeById() {

            long cakeId = givenTheFollowingCakeExistsInDatabase(aCake().title("some title").description("some description").build()).getId();

            getCakeByIdRequest(cakeId)
                    .headers(CakeControllerTest.this::withBasicAuth)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody()
                    //
                    .jsonPath("$.id").isEqualTo(cakeId)
                    .jsonPath("$.title").isEqualTo("some title")
                    .jsonPath("$.description").isEqualTo("some description");
        }

        @Test
        void getCakeByIdReturnsNotFoundWhenCakeIsNotFound() {

            getCakeByIdRequest(UNKNOWN_CAKE_ID)
                    .headers(CakeControllerTest.this::withBasicAuth)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody().isEmpty();
        }

        @Test
        void returnsUnauthorizedWhenAuthorizationHeaderIsMissing() {

            getCakeByIdRequest(UNKNOWN_CAKE_ID)
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody().isEmpty();
        }

        @Test
        void returnsUnauthorizedWhenUsernameAndPasswordAreIncorrect() {

            getCakeByIdRequest(UNKNOWN_CAKE_ID)
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody().isEmpty();
        }

        private WebTestClient.RequestHeadersSpec<?> getCakeByIdRequest(long cakeId) {
            return httpClient.get().uri("/cakes/" + cakeId);
        }
    }

    @Nested
    public class CreateCake {

        @Test
        void createCake() {

            createCakeRequest()
                    .headers(CakeControllerTest.this::withBasicAuth)
                    .contentType(APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Map.of(
                            "title", "new title",
                            "description", "new description"
                    )))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().contentType(APPLICATION_JSON)
                    .expectBody()
                    //
                    .jsonPath("$.title").isEqualTo("new title")
                    .jsonPath("$.description").isEqualTo("new description")
                    .jsonPath("$.id").value(id -> {
                        Cake savedCake = cakeRepository.findById(parseLong(id.toString())).get();
                        assertThat(savedCake)
                                .isEqualTo(new Cake(parseLong(id.toString()), "new title", "new description"));
                    });
        }

        @Test
        void returnsUnauthorizedWhenAuthorizationHeaderIsMissing() {

            createCakeRequest()
                    .contentType(APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Map.of(
                            "title", "new title",
                            "description", "new description"
                    )))
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody().isEmpty();
        }

        @Test
        void returnsUnauthorizedWhenUsernameAndPasswordAreIncorrect() {

            createCakeRequest()
                    .headers(headers -> headers.setBasicAuth("some-incorrect-username", "some-incorrect-password"))
                    .contentType(APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Map.of(
                            "title", "new title",
                            "description", "new description"
                    )))
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody().isEmpty();
        }

        private WebTestClient.RequestBodySpec createCakeRequest() {
            return httpClient.post().uri("/cakes");
        }
    }

    @Nested
    public class UpdateCake {

        @Test
        void updateCake() {

            long cakeId = givenTheFollowingCakeExistsInDatabase(aCake().title("existing title").description("existing description").build()).getId();

            updateCakeRequest(cakeId)
                    .headers(CakeControllerTest.this::withBasicAuth)
                    .contentType(APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Map.of(
                            "title", "updated title",
                            "description", "updated description"
                    )))
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            Cake savedCake = cakeRepository.findById(cakeId).get();
            assertThat(savedCake).isEqualTo(new Cake(cakeId, "updated title", "updated description"));
        }

        @Test
        void updateCakeReturnsNotFoundWhenCakeIsNotFound() {

            updateCakeRequest(UNKNOWN_CAKE_ID)
                    .headers(CakeControllerTest.this::withBasicAuth)
                    .contentType(APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Map.of(
                            "title", "updated title",
                            "description", "updated description"
                    )))
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody().isEmpty();
        }

        @Test
        void returnsUnauthorizedWhenAuthorizationHeaderIsMissing() {

            updateCakeRequest(UNKNOWN_CAKE_ID)
                    .contentType(APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Map.of(
                            "title", "updated title",
                            "description", "updated description"
                    )))
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody().isEmpty();
        }

        @Test
        void returnsUnauthorizedWhenUsernameAndPasswordAreIncorrect() {

            updateCakeRequest(UNKNOWN_CAKE_ID)
                    .headers(headers -> headers.setBasicAuth("some-incorrect-username", "some-incorrect-password"))
                    .contentType(APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Map.of(
                            "title", "updated title",
                            "description", "updated description"
                    )))
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody().isEmpty();
        }

        private WebTestClient.RequestBodySpec updateCakeRequest(long cakeId) {
            return httpClient.put().uri("/cakes/" + cakeId);
        }
    }

    @Nested
    public class DeleteCake {

        @Test
        void deleteCake() {

            long cakeId = givenTheFollowingCakeExistsInDatabase(aCake().title("existing title").description("existing description").build()).getId();

            deleteCakeRequest(cakeId)
                    .headers(CakeControllerTest.this::withBasicAuth)
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            assertThat(cakeRepository.findById(cakeId)).isEmpty();
        }

        @Test
        void deleteCakeReturnsNotFoundWhenCakeIsNotFound() {

            deleteCakeRequest(UNKNOWN_CAKE_ID)
                    .headers(CakeControllerTest.this::withBasicAuth)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody().isEmpty();
        }

        @Test
        void returnsUnauthorizedWhenAuthorizationHeaderIsMissing() {

            deleteCakeRequest(UNKNOWN_CAKE_ID)
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody().isEmpty();
        }

        @Test
        void returnsUnauthorizedWhenUsernameAndPasswordAreIncorrect() {

            deleteCakeRequest(UNKNOWN_CAKE_ID)
                    .headers(headers -> headers.setBasicAuth("some-incorrect-username", "some-incorrect-password"))
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody().isEmpty();
        }

        private WebTestClient.RequestHeadersSpec<?> deleteCakeRequest(long cakeId) {
            return httpClient.delete().uri("/cakes/" + cakeId);
        }
    }

    private Cake givenTheFollowingCakeExistsInDatabase(Cake cake) {
        return cakeRepository.saveAndFlush(cake);
    }

    private Cake.CakeBuilder aCake() {
        return Cake.builder().id(0);
    }

    private void withBasicAuth(HttpHeaders headers) {
        headers.setBasicAuth("cake-user-test", "cake-password-test");
    }
}
