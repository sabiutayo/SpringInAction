package taco.data;

import taco.tacos.Ingredient;

import java.util.Optional;

public interface TacoOrderRepository {
    Iterable<Ingredient> findAll();
    Optional<Ingredient> findById(String id);
    
}
