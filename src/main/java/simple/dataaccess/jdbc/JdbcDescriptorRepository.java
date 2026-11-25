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
                UPDATE descriptors
                SET quantity = descriptors.quantity + ?
                FROM descriptor_barcodes
                WHERE descriptor_barcodes.descriptor_id = descriptors.descriptor_id
                  AND descriptor_barcodes.barcode_key = ?
                  AND descriptor_barcodes.office_id = ?
                  AND descriptors.is_loanable = false
                  AND descriptors.quantity IS NOT NULL
                RETURNING descriptors.*
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
                SELECT descriptors.*
                FROM descriptors
                JOIN descriptor_barcodes
                    ON descriptors.descriptor_id = descriptor_barcodes.descriptor_id
                    AND descriptors.office_id = descriptor_barcodes.office_id
                WHERE descriptor_barcodes.barcode_key = ?
                    AND descriptor_barcodes.office_id = ?;
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
