package simple.dataaccess.jdbc;

import java.sql.*;
import java.util.Optional;
import java.util.ArrayList;

import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.LoanableMap;
import simple.domain.dao.LoanableDAO;
import simple.domain.exception.AlreadyExistsException;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Loanable;
import simple.domain.itemmanagement.LoanableRegistration;

public class JdbcLoanableDAO implements LoanableDAO {

    public Optional<Loanable> findByBarcode(String barcode) {
        String sql = """
                SELECT loanables.*, descriptors.*
                FROM loanables
                JOIN descriptors ON descriptors.descriptor_id = loanables.descriptor_id
                WHERE loanables.loanable_barcode = ?
                  AND loanables.loanable_deleted IS NULL
                """;

        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection();
                PreparedStatement statement = conn.prepareStatement(sql);) {
            statement.setString(1, barcode);
            try (ResultSet result = statement.executeQuery();) {
                return result.next() ? Optional.of(LoanableMap.mapRow(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find loanable by barcode: Unresponsive database", e);
        }
    }

    public ArrayList<Loanable> findAllByDescriptor(int descriptorId) {
        String sql = """
                SELECT *
                FROM loanables
                WHERE descriptor_id = ?
                AND loanable_deleted IS NULL
                ORDER BY unit_number
                """;

        Databaseconnection db = new Databaseconnection();

        ArrayList<Loanable> loanables = new ArrayList<>();

        try (Connection conn = db.getDbConnection();
                PreparedStatement statement = conn.prepareStatement(sql);) {
            statement.setInt(1, descriptorId);
            try (ResultSet result = statement.executeQuery();) {
                while (result.next()) {
                    loanables.add(LoanableMap.mapRow(result));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find loanable by desciptor id: Unresponsive database", e);
        }

        return loanables;

    }

    public void create(LoanableRegistration loanableRegistration, int unitNumber) {
        String sql = """
                INSERT INTO loanables
                (loanable_barcode, unit_number, loanable_info_field, descriptor_id, shelf_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, loanableRegistration.barcode());
            statement.setInt(2, unitNumber);
            statement.setString(3, loanableRegistration.infoField());
            statement.setInt(4, loanableRegistration.descriptorId());
            statement.setInt(5, loanableRegistration.shelfId());

            statement.executeUpdate();
        } catch (SQLException e) {
            // 23505 code for unique constraint broken
            if ("23505".equals(e.getSQLState())) {
                throw new AlreadyExistsException("Unique constraint failed", e);
            }
            throw new DataAccessException("Failed to create loanable item", e);
        }
    }
}
