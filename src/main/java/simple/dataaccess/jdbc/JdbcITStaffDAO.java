package simple.dataaccess.jdbc;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.ArrayList;

import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.DescriptorMap;
import simple.dataaccess.jdbc.rowMapping.ITStaffMap;
import simple.dataaccess.jdbc.rowMapping.LoanableMap;
import simple.domain.dao.ITStaffDAO;
import simple.domain.employee.ITStaff;
import simple.domain.employee.ITStaffRegistration;
import simple.domain.exception.AffectedRowsException;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Inventory;
import simple.domain.exception.AlreadyExistsException;

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
                return result.next() ? Optional.of(ITStaffMap.mapRow(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find IT staff by username and office", e);
        }
    }

    public void create(ITStaffRegistration itStaffRegistration) {

        String sql = """
                INSERT INTO it_staffs
                (username, first_name, last_name, office_id)
                VALUES (?, ?, ?, ?)
                """;
        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, itStaffRegistration.username());
            statement.setString(2, itStaffRegistration.firstName());
            statement.setString(3, itStaffRegistration.lastName());
            statement.setInt(4, itStaffRegistration.officeId());

            statement.executeUpdate();
        } catch (SQLException e) {
            // 23505 code for unique constraint broken
            if ("23505".equals(e.getSQLState())) {
                throw new AlreadyExistsException("Username needs to be unique", e);
            }
            throw new DataAccessException("Failed to create ITStaff", e);
        }

    }

    public void softDelete(int itStaffId) {
        String sql = """
                UPDATE it_staffs
                SET it_staff_deleted = ?
                WHERE it_staff_id = ?
                AND it_staff_deleted IS NULL
                """;
        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setObject(1, OffsetDateTime.now());
                statement.setInt(2, itStaffId);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected != 1) {
                    throw new AffectedRowsException("Expected 1 row affected, but was " + rowsAffected);
                }

                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete ITStaff", e);
        }

    }

    public ArrayList<ITStaff> findAll(int officeId) {

        String sql = """
                SELECT *
                FROM it_staffs
                WHERE it_staff_deleted IS NULL
                AND office_id = ?
                """;

        ArrayList<ITStaff> itStaffs = new ArrayList<>();

        Databaseconnection databaseConnection = new Databaseconnection();

        try (Connection connection = databaseConnection.getDbConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, officeId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    itStaffs.add(ITStaffMap.mapRow(result));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load ITStaffs for office " + officeId, e);
        }

        return itStaffs;
    }

}
