package io.cip.services.cake.cucumber.stepdefs;

import io.cip.services.cake.cucumber.CakeDataTableEntities.TestCakeDatabaseEntity;
import io.cip.services.cake.cucumber.CakeDataTableEntities.TestCreateCake;
import io.cip.services.cake.cucumber.CakeStateHolder;
import io.cip.services.cake.repository.CakeRepository;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.Map;

import static io.cip.services.cake.cucumber.CakeStateHolder.lastHttpResponseStatusCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class CakeStepDefs {

    @Autowired
    private WebTestClient httpClient;

    @Autowired
    private CakeRepository cakeRepository;

    @Before
    public void setup(Scenario scenario) {
        // For non-parallel mode
        CakeStateHolder.clear();
    }

    @Given("There is no cake in the database")
    public void thereIsNoCakeInTheDatabase() {
        cakeRepository.deleteAllInBatch();
    }

    @When("I create a cake with the following details")
    public void iCreateACakeWithTheFollowingDetails(@Transpose TestCreateCake createCake) {
        WebTestClient.ResponseSpec response = httpClient.post().uri("/cakes")
                .headers(CakeStepDefs.this::withBasicAuth)
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of(
                        "title", createCake.title(),
                        "description", createCake.description()
                )))
                .exchange();

        CakeStateHolder.setLastHttpResponse(response);
    }

    @When("I create the following cakes")
    public void iCreateTheFollowingCakes(List<TestCreateCake> createCakes) {
        createCakes.forEach(this::iCreateACakeWithTheFollowingDetails);
    }

    @Then("The HTTP response status code is {int}")
    public void theHTTPResponseStatusCodeIs(int statusCode) {
        assertThat(lastHttpResponseStatusCode()).isEqualTo(statusCode);
    }

    @Then("The following cake should exist in the database")
    public void theFollowingCakeShouldExistInTheDatabase(@Transpose TestCakeDatabaseEntity cakeDatabaseEntity) {
        assertThat(cakeRepository.findByTitle(cakeDatabaseEntity.title()))
                .isPresent()
                .get()
                .satisfies(cake -> {
                    assertThat(cake.getTitle()).isEqualTo(cakeDatabaseEntity.title());
                    assertThat(cake.getDescription()).isEqualTo(cakeDatabaseEntity.description());
                });
    }

    @Then("The following cakes should exist in the database")
    public void theFollowingCakesShouldExistInTheDatabase(List<TestCakeDatabaseEntity> cakeDatabaseEntities) {
        cakeDatabaseEntities.forEach(this::theFollowingCakeShouldExistInTheDatabase);
    }

    private void withBasicAuth(HttpHeaders headers) {
        headers.setBasicAuth("cake-user-test", "cake-password-test");
    }
}
