package taco.data;

import taco.tacos.Ingredient;
import taco.tacos.TacoOrder;

import java.util.Optional;

public interface OrderRepository {

    TacoOrder save(TacoOrder order);

}
