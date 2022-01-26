package taco.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import taco.data.IngredientRepository;
import taco.tacos.Ingredient;
import taco.tacos.Taco;
import taco.tacos.TacoOrder;

import javax.validation.Valid;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static taco.tacos.Ingredient.Type;

@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder")
public class DesignTacoController {

    private final IngredientRepository ingredientRepository;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @ModelAttribute
    public void addIngredientsToModel(Model model) {
        Iterable<Ingredient> ingredients = ingredientRepository.findAll();
        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(type, ingredients));
        }

    }

    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    @ModelAttribute(name = "tacoOrder")
    TacoOrder order() {
        return new TacoOrder();
    }

    private Iterable<Ingredient> filterByType(Type type, Iterable<Ingredient> ingredients) {
        return StreamSupport.stream(ingredients.spliterator(), false).filter(ingredient -> ingredient.getType().equals(type)).collect(Collectors.toList());
    }

    @GetMapping
    public String showDesignForm(Model model) {
        return "design.html";
    }

    @PostMapping
    public String processTaco(@Valid Taco taco, Errors errors, @ModelAttribute TacoOrder tacoOrder, Model model) {
        if (errors.hasErrors()) {
            return "/design";
        }

        tacoOrder.addTaco(taco);

        return "redirect:/orders/current";
    }


}
