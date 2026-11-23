package simple.dataaccess.jdbc;

import java.util.Optional;

import simple.dataaccess.config.Databaseconnection;
import simple.domain.dao.ITStaffDAO;
import simple.domain.employee.ITStaff;
import simple.domain.exception.DataAccessException;

import java.sql.*;
import simple.dataaccess.jdbc.rowMapping.ItstaffMap;

public class JdbcITStaffDAO implements ITStaffDAO {

    @Override
    public Optional<ITStaff> findByUsername(String username, int officeId) {
        String sql = """
                SELECT *
                FROM it_staffs
                WHERE username = ?
                  AND office_id = ?
                  AND it_staff_deleted is NULL
                """;

        Databaseconnection db = new Databaseconnection();

        try (Connection connection = db.getDbConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setInt(2, officeId);

            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(ItstaffMap.mapRow(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: Failed to find IT staff by username and office", e);
        }   
    }
}