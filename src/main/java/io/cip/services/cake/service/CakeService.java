package io.cip.services.cake.service;

import io.cip.services.cake.exception.CakeNotFoundException;
import io.cip.services.cake.model.cake.CakeResponse;
import io.cip.services.cake.model.cake.CakesResponse;
import io.cip.services.cake.model.cake.CreateCakeRequest;
import io.cip.services.cake.model.cake.UpdateCakeRequest;
import io.cip.services.cake.repository.CakeRepository;
import io.cip.services.cake.repository.model.Cake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Slf4j
@Service
public class CakeService {

    private final CakeRepository cakeRepository;

    public CakesResponse getCakes() {
        return new CakesResponse(cakeRepository.findAll().stream()
                .map(this::cakeResponse)
                .sorted(Comparator.comparing(CakeResponse::title))
                .collect(toList()));
    }

    public CakeResponse getCakeById(long cakeId) {
        return cakeResponse(findExistingCake(cakeId));
    }

    public CakeResponse createCake(CreateCakeRequest createCakeRequest) {
        Cake cake = cakeRepository.save(Cake.builder()
                .title(createCakeRequest.title())
                .description(createCakeRequest.description())
                .build());
        return cakeResponse(cake);
    }

    public CakeResponse updateCake(long cakeId, UpdateCakeRequest updateCakeRequest) {
        Cake existingCake = findExistingCake(cakeId);

        Cake updateCake = cakeRepository.save(
                existingCake.toBuilder()
                        .title(updateCakeRequest.title())
                        .description(updateCakeRequest.description())
                        .build()
        );

        return cakeResponse(updateCake);
    }

    public void deleteCake(long cakeId) {
        cakeRepository.delete(findExistingCake(cakeId));
    }

    private Cake findExistingCake(long cakeId) {
        return cakeRepository.findById(cakeId).orElseThrow(() -> {
            log.error("cake with id not found {}", cakeId);
            throw new CakeNotFoundException();
        });
    }

    private CakeResponse cakeResponse(Cake cake) {
        return new CakeResponse(cake.getId(), cake.getTitle(), cake.getDescription());
    }

}
