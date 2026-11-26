package simple.dataaccess.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import simple.dataaccess.config.Databaseconnection;
import simple.domain.dao.ShelfDAO;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Shelf;

public class JdbcShelfDAO implements ShelfDAO {

    @Override
    public List<Shelf> findAllByOffice(int officeId) {
        String sql = """
                SELECT shelf_id, shelf_name
                FROM shelves
                WHERE office_id = ?
                  AND shelf_deleted IS NULL
                ORDER BY shelf_name
                """;

        Databaseconnection db = new Databaseconnection();
        List<Shelf> shelves = new ArrayList<>();

        try (Connection connection = db.getDbConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, officeId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    shelves.add(new Shelf(result.getInt("shelf_id"), result.getString("shelf_name")));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: Failed to load shelves for office " + officeId, e);
        }
        return shelves;
    }
}
