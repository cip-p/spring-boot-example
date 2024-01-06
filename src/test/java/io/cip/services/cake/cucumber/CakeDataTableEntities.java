package io.cip.services.cake.cucumber;

public class CakeDataTableEntities {

    public record TestCreateCake(String title, String description) {
    }

    public record TestCakeDatabaseEntity(String title, String description) {
    }

}
