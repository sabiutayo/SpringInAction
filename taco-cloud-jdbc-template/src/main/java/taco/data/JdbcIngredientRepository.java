package taco.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import taco.tacos.Ingredient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcIngredientRepository implements IngredientRepository {
    public final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcIngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<Ingredient> findAll() {
        String sql = "SELECT * FROM Ingredient";
       List<Ingredient> result = jdbcTemplate.query(sql, this::mapRowToIngredient);
        return result;
    }

    @Override
    public Optional<Ingredient> findById(String id) {
        String sql = "SELECT * FROM Ingredient WHERE id=?";
       List<Ingredient> result = jdbcTemplate.query(sql,this::mapRowToIngredient,id);
                return result.size() == 0 ?
                        Optional.empty() :
                        Optional.of(result.get(0));
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        String sql = "INSERT INTO Ingredient (id, name, type) values(?,?,?)";
        jdbcTemplate.update(sql,
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getType().toString());
        return ingredient;
    }

    private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
        return new Ingredient(rs.getString("id"),
                rs.getString("name"),
                Ingredient.Type.valueOf(rs.getString("type")));
    }

}
