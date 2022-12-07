package io.cip.services.cake.repository;

import io.cip.services.cake.repository.model.Cake;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CakeRepository extends JpaRepository<Cake, Long> {
}
