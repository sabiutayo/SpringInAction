package taco.tacos.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import taco.tacos.Ingredient;
import taco.tacos.Ingredient.Type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcIngredientRepository implements IngredientRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcIngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<Ingredient> findAll() {
        List<Ingredient> result = jdbcTemplate.query(
                "SELECT id, name, type FROM Ingredient",
                this::mapRowToIngredient
        );

        return null;
    }

    @Override
    public Optional<Ingredient> findById(String id) {
        List<Ingredient> result = jdbcTemplate.query(
                "SELECT id, name, type FROM Ingredient WHERE id=?",
                this::mapRowToIngredient,
                id);
        return result.isEmpty() ?
                Optional.empty() :
                Optional.of(result.get(0));

    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        jdbcTemplate.update(
                "INSERT INTO Ingredient VALUES (?,?,?;)",
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getType().toString());


        return ingredient;
    }

    private Ingredient mapRowToIngredient(ResultSet row, int rowNum) throws SQLException {
        return new Ingredient(
                row.getString("id"),
                row.getString("name"),
                Type.valueOf(row.getString("type")));
    }

}
