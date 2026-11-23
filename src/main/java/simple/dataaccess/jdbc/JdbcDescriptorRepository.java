package simple.dataaccess.jdbc;

import java.sql.*;
import java.util.Optional;
import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.DescriptorMap;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.repository.DescriptorRepository;

public class JdbcDescriptorRepository implements DescriptorRepository {

    @Override
    public Optional<Descriptor> changeQuantity(String barcode, int officeId, int delta) {
        String sql = """
                UPDATE descriptors AS d
                SET quantity = d.quantity + ?
                FROM descriptor_barcodes AS b
                WHERE b.descriptor_id = d.descriptor_id
                  AND b.barcode_key = ?
                  AND b.office_id = ?
                  AND d.is_loanable = false
                  AND d.quantity IS NOT NULL
                RETURNING d.*
                """;

        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, delta);
            statement.setString(2, barcode);
            statement.setInt(3, officeId);

            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(DescriptorMap.mapRow(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: Failed to change descriptor quantity", e);
        }
    }

    @Override
    public Optional<Descriptor> findByBarcode(String barcode, int office_id) {
        Databaseconnection db = new Databaseconnection();
        String sql = """
                SELECT d.*
                FROM descriptors d
                JOIN descriptor_barcodes db
                    ON d.descriptor_id = db.descriptor_id
                    AND d.office_id = db.office_id
                WHERE db.barcode_key = ?
                    AND db.office_id = ?;
                """;

        try (Connection conn = db.getDbConnection();
                PreparedStatement statement = conn.prepareStatement(sql); ) {
            statement.setString(1, barcode);
            statement.setInt(2, office_id);
            try (ResultSet result = statement.executeQuery(); ) {
                return result.next() ? Optional.of(DescriptorMap.mapRow(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Database error: Failed to find descriptor by barcode: Unresponsive database", e);
        }
    }
}
