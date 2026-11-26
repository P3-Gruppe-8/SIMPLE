package simple.dataaccess.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import simple.dataaccess.config.Databaseconnection;
import simple.domain.dao.OfficeDAO;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Office;

public class JdbcOfficeDAO implements OfficeDAO {

    @Override
    public List<Office> findAll() {
        String sql = """
                SELECT office_id, office_name
                FROM offices
                WHERE office_deleted IS NULL
                ORDER BY office_name
                """;

        Databaseconnection db = new Databaseconnection();
        List<Office> offices = new ArrayList<>();

        try (Connection connection = db.getDbConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                offices.add(new Office(result.getInt("office_id"), result.getString("office_name")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: Failed to load offices", e);
        }
        return offices;
    }
}
