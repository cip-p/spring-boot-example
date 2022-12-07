package io.cip.services.cake.controller;

import io.cip.services.cake.exception.CakeNotFoundException;
import io.cip.services.cake.model.cake.CakeResponse;
import io.cip.services.cake.model.cake.CakesResponse;
import io.cip.services.cake.model.cake.CreateCakeRequest;
import io.cip.services.cake.model.cake.UpdateCakeRequest;
import io.cip.services.cake.service.CakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/cakes")
public class CakeController {

    private final CakeService cakeService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public CakesResponse getCakes() {
        log.info("getting all cakes");

        return cakeService.getCakes();
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CakeResponse> getCakeById(@PathVariable long id) {
        log.info("getting cake with id {}", id);

        return ResponseEntity.ok(cakeService.getCakeById(id));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public CakeResponse createCake(@RequestBody CreateCakeRequest createCakeRequest) {
        log.info("creating cake {}", createCakeRequest);

        CakeResponse cake = cakeService.createCake(createCakeRequest);

        log.info("cake created, cake id {}", cake.id());

        return cake;
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateCake(@PathVariable long id, @RequestBody UpdateCakeRequest updateCakeRequest) {
        log.info("updating cake with id {}: {}", id, updateCakeRequest);

        cakeService.updateCake(id, updateCakeRequest);

        log.info("cake updated, cake id {}", id);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCake(@PathVariable long id) {
        log.info("deleting cake with id {}", id);

        cakeService.deleteCake(id);

        log.info("cake deleted, cake id {}", id);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @ExceptionHandler(CakeNotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    private void cakeNotFoundException() {
    }
}
