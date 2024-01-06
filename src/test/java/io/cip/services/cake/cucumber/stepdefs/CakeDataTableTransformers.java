package io.cip.services.cake.cucumber.stepdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cip.services.cake.cucumber.CakeDataTableEntities.TestCreateCake;
import io.cucumber.java.DataTableType;
import io.cucumber.java.DefaultDataTableCellTransformer;
import io.cucumber.java.DefaultDataTableEntryTransformer;
import io.cucumber.java.DefaultParameterTransformer;

import java.lang.reflect.Type;
import java.util.Map;

public class CakeDataTableTransformers {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DefaultParameterTransformer
    @DefaultDataTableEntryTransformer
    @DefaultDataTableCellTransformer
    public Object defaultTransformer(Object fromValue, Type toValueType) {
        return objectMapper.convertValue(fromValue, objectMapper.constructType(toValueType));
    }

    @DataTableType
    public TestCreateCake createCakeTransformer(Map<String, String> entry) {
        return new TestCreateCake(
                entry.get("title"),
                entry.get("description"
                )
        );
    }
}
