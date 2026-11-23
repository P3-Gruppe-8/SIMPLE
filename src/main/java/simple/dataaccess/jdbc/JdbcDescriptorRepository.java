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
    public boolean changeQuantity(Connection conn, String barcode, int officeId, int delta) {
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

        try (PreparedStatement statement = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            statement.setInt(1, delta);
            statement.setString(2, barcode);
            statement.setInt(3, officeId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return true; // En række blev opdateret
                } else {
                    return false; // Ingen rækker blev opdateret
                }
            }

        } catch (SQLException e) {
            if ("23514".equals(e.getSQLState())) {
                throw new DataAccessException("Failed to change descriptor quantity", e);
            }

            return false;
        }
    }

    @Override
    public Optional<Descriptor> findByBarcode(Connection conn, String barcode, int office_id) {
        String sql = """
                SELECT d.*
                FROM descriptors d
                JOIN descriptor_barcodes db
                    ON d.descriptor_id = db.descriptor_id
                    AND d.office_id = db.office_id
                WHERE db.barcode_key = ?
                    AND db.office_id = ?;
                """;

        try (PreparedStatement statement = conn.prepareStatement(sql);) {
            statement.setString(1, barcode);
            statement.setInt(2, office_id);
            try (ResultSet result = statement.executeQuery();) {
                return result.next() ? Optional.of(DescriptorMap.mapRow(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Database error: Failed to find descriptor by barcode: Unresponsive database", e);
        }
    }

    public boolean softDelete(Connection conn, int descriptor_id, int office_id) {
        String sql = """
                UPDATE descriptors
                SET descriptor_deleted = now()
                WHERE descriptor_id = ?
                    AND office_id = ?
                """;

        try (PreparedStatement statement = conn.prepareStatement(sql);) {
            statement.setInt(1, descriptor_id);
            statement.setInt(2, office_id);
            int result = statement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete descriptor", e);
        }
    }

    public int createNew(Connection conn, String descriptor_name, boolean is_loanable, String info_field,
            int office_id) {
        String sql = """
                INSERT INTO descriptors (descriptor_name, is_loanable, quantity, info_field, office_id)
                VALUES (?, ?, 0, ?, ?)
                RETURNING descriptor_id
                """;

        try (PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, descriptor_name);
            statement.setBoolean(2, is_loanable);
            statement.setString(3, info_field);
            statement.setInt(4, office_id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("descriptor_id");
                }
            }

            throw new DataAccessException("Failed to create descriptor: Unable to get new descriptor ID");

        } catch (SQLException e) {
            throw new DataAccessException("Failed to create descriptor", e);
        }
    }

    public boolean addBarcode(Connection conn, int descriptor_id, String barcode, int office_id) {
        String sql = """
                INSERT INTO descriptor_barcodes (barcode_key, descriptor_id, office_id)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement statement = conn.prepareStatement(sql);) {
            statement.setString(1, barcode);
            statement.setInt(2, descriptor_id);
            statement.setInt(3, office_id);
            int result = statement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add barcode", e);
        }
    }
}
