package io.cip.services.cake.cucumber;

import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

public class CakeStateHolder {

    private static class State {
        private WebTestClient.ResponseSpec lastHttpResponse;
    }

    private static final ThreadLocal<State> store = ThreadLocal.withInitial(State::new);

    private static State state() {
        return store.get();
    }

    public static void clear() {
        System.out.println("clearing the state");
        store.remove();
    }

    public static void setLastHttpResponse(WebTestClient.ResponseSpec response) {
        state().lastHttpResponse = response;
    }

    public static int lastHttpResponseStatusCode() {
        return Optional.ofNullable(state().lastHttpResponse)
                .map(response -> response.returnResult(Object.class).getStatus().value())
                .orElse(0);
    }
}
