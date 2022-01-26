package taco.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import taco.tacos.Ingredient;
import taco.tacos.Taco;
import taco.tacos.data.IngredientRepository;

import javax.validation.Valid;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static taco.tacos.Ingredient.Type;

@Slf4j
@Controller
@SessionAttributes("tacoOrder")
@RequestMapping("/design")
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    @ModelAttribute
    public void addIngredientsToModel(Model model) {
        Iterable<Ingredient> ingredients = ingredientRepo.findAll();
        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(type, ingredients));
        }

    }

    private Iterable<Ingredient> filterByType(Type type, Iterable<Ingredient> ingredients) {
        return StreamSupport.stream(ingredients.spliterator(), false)
                .filter(ingredient -> ingredient.getType().equals(type))
                .collect(Collectors.toList());
    }

    @GetMapping
    public String showDesignForm(Model model) {
        model.addAttribute("taco", new Taco());
        return "design.html";
    }

    @PostMapping
    public String processTaco(@Valid @ModelAttribute("taco") Taco taco, Errors errors) {
        if (errors.hasErrors()) {
            return "/design";
        }
        //save taco
        log.info("Processing taco: " + taco);
        return "redirect:/orders/current";
    }


}
