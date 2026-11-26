package simple.dataaccess.jdbc;

import java.sql.*;
import java.util.Optional;
import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.DescriptorMap;
import simple.domain.exception.AffectedRowsException;
import simple.domain.exception.DataAccessException;
import simple.domain.exception.QuantityException;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.repository.DescriptorRepository;

public class JdbcDescriptorRepository implements DescriptorRepository {

    @Override
    public Optional<Descriptor> changeQuantity(String barcode, int officeId, int delta) {
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(false);

        String sql = """
                UPDATE descriptors AS d
                SET quantity = d.quantity + ?
                FROM descriptor_barcodes AS b
                WHERE b.descriptor_id = d.descriptor_id
                  AND b.barcode_key = ?
                  AND b.office_id = ?
                  AND d.is_loanable = false
                  AND d.quantity IS NOT NULL
                  AND d.descriptor_deleted IS NULL
                RETURNING d.*
                """;

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, delta);
            statement.setString(2, barcode);
            statement.setInt(3, officeId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    db.executeCommit(conn);
                    db.closeConnection(conn);
                    return Optional.of(DescriptorMap.mapRow(result));
                } else {
                    db.executeRollback(conn);
                    db.closeConnection(conn);
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            if ("23514".equals(e.getSQLState())) {
                db.executeRollback(conn);
                db.closeConnection(conn);
                throw new QuantityException("Not enough in stock", e);
            }
            db.executeRollback(conn);
            db.closeConnection(conn);
            throw new DataAccessException("Failed to change descriptor quantity", e);
        }
    }

    @Override
    public Optional<Descriptor> findByBarcode(String barcode, int office_id) {
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(true);

        String sql = """
                SELECT d.*
                FROM descriptors d
                JOIN descriptor_barcodes db
                    ON d.descriptor_id = db.descriptor_id
                    AND d.office_id = db.office_id
                WHERE db.barcode_key = ?
                    AND db.office_id = ?
                    AND d.descriptor_deleted IS NULL;
                """;

        try (PreparedStatement statement = conn.prepareStatement(sql); ) {
            statement.setString(1, barcode);
            statement.setInt(2, office_id);
            try (ResultSet result = statement.executeQuery()) {
                Optional<Descriptor> descriptor = Optional.empty();
                if (result.next()) {
                    descriptor = Optional.of(DescriptorMap.mapRow(result));
                }
                db.closeConnection(conn);
                return descriptor;
            }
        } catch (SQLException e) {
            db.closeConnection(conn);
            throw new DataAccessException("Failed to find descriptor by barcode: Unresponsive database", e);
        }
    }

    public boolean softDelete(int descriptor_id, int office_id) {
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(false);

        String sqlDeleteDescriptor = """
                UPDATE descriptors
                SET descriptor_deleted = now()
                WHERE descriptor_id = ?
                    AND office_id = ?
                """;

        String sqlDeleteBarcodes = """
                DELETE FROM descriptor_barcodes
                WHERE descriptor_id = ?
                    AND office_id = ?
                """;

        try (PreparedStatement deleteDescriptor = conn.prepareStatement(sqlDeleteDescriptor);
                PreparedStatement deleteBarcodes = conn.prepareStatement(sqlDeleteBarcodes)) {
            deleteDescriptor.setInt(1, descriptor_id);
            deleteDescriptor.setInt(2, office_id);
            if (deleteDescriptor.executeUpdate() != 1) {
                db.executeRollback(conn);
                db.closeConnection(conn);
                throw new AffectedRowsException("Failed to delete descriptor: 0 or more than 1 row affected");
            }

            deleteBarcodes.setInt(1, descriptor_id);
            deleteBarcodes.setInt(2, office_id);
            deleteBarcodes.executeUpdate();

            db.executeCommit(conn);
            db.closeConnection(conn);
            return true;
        } catch (SQLException e) {
            db.executeRollback(conn);
            db.closeConnection(conn);
            throw new DataAccessException("Failed to delete descriptor", e);
        }
    }

    public boolean createNew(
            String descriptor_name,
            boolean is_loanable,
            String info_field,
            int office_id,
            String barcode,
            int shelf_id) {

        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.newConnection(false)) {
            try {
                String sqlCreateDescriptor = """
                        INSERT INTO descriptors
                        (descriptor_name, is_loanable, quantity, info_field, office_id)
                        VALUES (?, ?, 0, ?, ?)
                        RETURNING descriptor_id
                        """;

                int descriptor_id;

                try (PreparedStatement statement = conn.prepareStatement(sqlCreateDescriptor)) {
                    statement.setString(1, descriptor_name);
                    statement.setBoolean(2, is_loanable);
                    statement.setString(3, info_field);
                    statement.setInt(4, office_id);

                    try (ResultSet rs = statement.executeQuery()) {
                        if (!rs.next()) {
                            db.executeRollback(conn);
                            db.closeConnection(conn);
                            throw new SQLException("Unable to create descriptor: No descriptor ID returned");
                        }
                        descriptor_id = rs.getInt("descriptor_id");
                    }
                }

                String sqlAddBarcode = """
                        INSERT INTO descriptor_barcodes
                        (barcode_key, descriptor_id, office_id)
                        VALUES (?, ?, ?)
                        """;

                try (PreparedStatement statement = conn.prepareStatement(sqlAddBarcode)) {
                    statement.setString(1, barcode);
                    statement.setInt(2, descriptor_id);
                    statement.setInt(3, office_id);

                    if (statement.executeUpdate() != 1) {
                        db.executeRollback(conn);
                        db.closeConnection(conn);
                        throw new SQLException("Unable to create descriptor: Failed to add barcode");
                    }
                }

                String sqlAddShelf = """
                        INSERT INTO descriptor_shelves (descriptor_id, shelf_id)
                        VALUES (?, ?)
                        """;

                try (PreparedStatement statement = conn.prepareStatement(sqlAddShelf)) {
                    statement.setInt(1, descriptor_id);
                    statement.setInt(2, shelf_id);

                    if (statement.executeUpdate() != 1) {
                        db.executeRollback(conn);
                        db.closeConnection(conn);
                        throw new SQLException("Unable to create descriptor: Failed to add shelf");
                    }
                }

                db.executeCommit(conn);
                db.closeConnection(conn);
                return true;

            } catch (SQLException e) {
                db.executeRollback(conn);
                db.closeConnection(conn);
                throw new DataAccessException("Unable to create descriptor: Failed to create descriptor", e);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create descriptor: Failed to connect to database", e);
        }
    }

    public boolean addBarcode(int descriptor_id, String barcode, int office_id) {
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(false);

        String sql = """
                INSERT INTO descriptor_barcodes (barcode_key, descriptor_id, office_id)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement statement = conn.prepareStatement(sql); ) {
            statement.setString(1, barcode);
            statement.setInt(2, descriptor_id);
            statement.setInt(3, office_id);
            if (statement.executeUpdate() == 1) {
                db.executeCommit(conn);
                db.closeConnection(conn);
                return true;
            } else {
                db.executeRollback(conn);
                db.closeConnection(conn);
                throw new AffectedRowsException("Failed to add barcode to descriptor: 0 or more than 1 row affected");
            }
        } catch (SQLException e) {
            db.executeRollback(conn);
            db.closeConnection(conn);
            throw new DataAccessException("Failed to add barcode to descriptor", e);
        }
    }
}
