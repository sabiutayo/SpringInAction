package taco.data;


import org.springframework.asm.Type;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import taco.tacos.IngredientRef;
import taco.tacos.Taco;
import taco.tacos.TacoOrder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private final JdbcOperations jdbcOperations;
    private final JdbcTemplate jdbcTemplate;

    public JdbcOrderRepository(JdbcOperations jdbcOperations, JdbcTemplate jdbcTemplate) {
        this.jdbcOperations = jdbcOperations;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public TacoOrder save(TacoOrder order) {
        String sql = "INSERT INTO Taco_Order "
                + "(delivery_Name, delivery_Street, delivery_City, delivery_State, delivery_Zip," +
                "cc_number, cc_expiration, cc_cvv, placed_at)" +
                " VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
                sql,
                VARCHAR, VARCHAR, VARCHAR, VARCHAR, VARCHAR,
                VARCHAR, VARCHAR, VARCHAR, TIMESTAMP
        );
        pscf.setReturnGeneratedKeys(true);
        order.setPlacedAt(new Date());

        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(Arrays.asList(
                order.getDeliveryName(),
                order.getDeliveryStreet(),
                order.getDeliveryCity(),
                order.getDeliveryState(),
                order.getDeliveryZip(),
                order.getCcNumber(),
                order.getCcExpiration(),
                order.getCcCVV(),
                order.getPlacedAt()
        ));
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psc, keyHolder);
        long orderId = keyHolder.getKey().longValue();
        order.setId(orderId);
        List<Taco> tacos = order.getTacos();
        int i = 0;
        for (Taco taco : tacos) {
            saveTaco(orderId, i++, taco);
        }
        return order;
    }

    private Taco saveTaco(long orderId, int tacoOrderKey, Taco taco) {
        String sql = "INSERT INTO Taco (name, taco_order, taco_order_key, created_at) VALUES(?,?,?,?)";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
                sql, VARCHAR, Type.LONG, Type.LONG, TIMESTAMP);
        pscf.setReturnGeneratedKeys(true);
        taco.setCreatedAt(new Date());
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
                Arrays.asList(
                        taco.getName(),
                        orderId,
                        tacoOrderKey,
                        taco.getCreatedAt()));
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psc, keyHolder);
        long tacoId = keyHolder.getKey().longValue();
        taco.setId(tacoId);
        saveIngredientRef(tacoId, taco.getIngredients());
        return taco;
    }

    private void saveIngredientRef(long tacoId, List<IngredientRef> ingredients) {
        int key = 0;
        for (IngredientRef ingredientRef : ingredients) {
            jdbcOperations.update(
                    "INSERT INTO Ingredient_Ref (ingredient, taco, taco_key)" +
                            "VALUES ( ?,?,? )",
                    ingredientRef.getIngredient(),tacoId,key++);

        }
    }
}
