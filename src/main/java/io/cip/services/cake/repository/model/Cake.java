package io.cip.services.cake.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@Entity
@Table(name = "cakes")
public class Cake {

    public Cake() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    private String description;
}
