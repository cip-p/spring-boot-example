package io.cip.services.cake.populator;

import io.cip.services.cake.CakeServiceSpringTest;
import io.cip.services.cake.repository.CakeRepository;
import io.cip.services.cake.repository.model.Cake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@CakeServiceSpringTest
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:cake-service-populator")
class CakeDatabasePopulatorTest {

    @Autowired
    private CakeRepository cakeRepository;

    @Test
    void populatesDatabaseWhenApplicationStarts() {

        List<Cake> cakes = cakeRepository.findAll();

        assertThat(cakes).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrder(
                        Cake.builder().title("cake with all fields").description("some desc 1").build(),
                        Cake.builder().title("cake with extra field").description("some desc 2").build(),
                        Cake.builder().title("cake with missing desc").description(null).build(),
                        Cake.builder().title("cake with null desc").description(null).build()
                );
    }

}
