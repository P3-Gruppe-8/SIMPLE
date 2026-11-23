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
                throw new DataAccessException("Failed to change descriptor quantity", e);
            }
            db.executeRollback(conn);
            db.closeConnection(conn);
            return Optional.empty();
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
                    AND db.office_id = ?;
                """;

        try (PreparedStatement statement = conn.prepareStatement(sql);) {
            statement.setString(1, barcode);
            statement.setInt(2, office_id);
            try (ResultSet result = statement.executeQuery()) {
                db.closeConnection(conn);
                if (result.next()) {
                    return Optional.of(DescriptorMap.mapRow(result));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            db.closeConnection(conn);
            throw new DataAccessException("Failed to find descriptor by barcode: Unresponsive database", e);
        }
    }

    public boolean softDelete(int descriptor_id, int office_id) {
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(false);

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
            if (result == 1) {
                db.executeCommit(conn);
                db.closeConnection(conn);
                return true;
            } else {
                db.executeRollback(conn);
                db.closeConnection(conn);
                throw new DataAccessException("Failed to delete descriptor: 0 or more than 1 row affected");
            }
        } catch (SQLException e) {
            db.executeRollback(conn);
            db.closeConnection(conn);
            throw new DataAccessException("Failed to delete descriptor", e);
        }
    }

    public boolean createNew(String descriptor_name, boolean is_loanable, String info_field, int office_id,
            String barcode, int shelf_id) {
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(false);

        String sqlCreateDescriptor = """
                INSERT INTO descriptors (descriptor_name, is_loanable, quantity, info_field, office_id)
                VALUES (?, ?, 0, ?, ?)
                RETURNING descriptor_id
                """;

        try (PreparedStatement statementCreateDescriptor = conn.prepareStatement(sqlCreateDescriptor)) {

            statementCreateDescriptor.setString(1, descriptor_name);
            statementCreateDescriptor.setBoolean(2, is_loanable);
            statementCreateDescriptor.setString(3, info_field);
            statementCreateDescriptor.setInt(4, office_id);

            int descriptor_id = 0;

            try (ResultSet rs = statementCreateDescriptor.executeQuery()) {
                if (rs.next()) {
                    descriptor_id = rs.getInt("descriptor_id");
                } else {
                    db.executeRollback(conn);
                    db.closeConnection(conn);
                    throw new DataAccessException("Failed to create descriptor: Unable to get descriptor ID");
                }
            } catch (SQLException e) {
                db.executeRollback(conn);
                db.closeConnection(conn);
                throw new DataAccessException("Failed to create descriptor: Unable to execute query", e);
            }

            String sqlAddBarcode = """
                    INSERT INTO descriptor_barcodes (barcode_key, descriptor_id, office_id)
                    VALUES (?, ?, ?)
                    """;

            try (PreparedStatement statementAddBarcode = conn.prepareStatement(sqlAddBarcode);) {
                statementAddBarcode.setString(1, barcode);
                statementAddBarcode.setInt(2, descriptor_id);
                statementAddBarcode.setInt(3, office_id);
                int result = statementAddBarcode.executeUpdate();
                if (result != 1) {
                    db.executeRollback(conn);
                    db.closeConnection(conn);
                    throw new DataAccessException("Failed to add barcode to descriptor: 0 or more than 1 row affected");
                }
            } catch (SQLException e) {
                db.executeRollback(conn);
                db.closeConnection(conn);
                throw new DataAccessException("Failed to add barcode to descriptor", e);
            }

            String sqlAddShelf = """
                    INSERT INTO descriptor_shelves (descriptor_id, shelf_id)
                    VALUES (?, ?)
                    """;
            try (PreparedStatement statementAddShelf = conn.prepareStatement(sqlAddShelf);) {
                statementAddShelf.setInt(1, descriptor_id);
                statementAddShelf.setInt(1, shelf_id);
                int result = statementAddShelf.executeUpdate();
                if (result != 1) {
                    db.executeRollback(conn);
                    db.closeConnection(conn);
                    throw new DataAccessException("Failed to add shelf to descriptor: 0 or more than 1 row affected");
                }
            } catch (SQLException e) {
                db.executeRollback(conn);
                db.closeConnection(conn);
                throw new DataAccessException("Failed to add shelf to descriptor", e);
            }

        } catch (SQLException e) {
            db.executeRollback(conn);
            db.closeConnection(conn);
            throw new DataAccessException("Failed to create descriptor", e);
        }
        db.executeCommit(conn);
        db.closeConnection(conn);
        return true;
    }

    // CREATE TABLE descriptor_shelves (
    // descriptor_id INTEGER NOT NULL REFERENCES descriptors (descriptor_id) ON
    // DELETE CASCADE,
    // shelf_id INTEGER NOT NULL REFERENCES shelves (shelf_id) ON DELETE CASCADE,
    // PRIMARY KEY (descriptor_id, shelf_id)
    // );

    public boolean addBarcode(int descriptor_id, String barcode, int office_id) {
        Databaseconnection db = new Databaseconnection();
        Connection conn = db.newConnection(false);

        String sql = """
                INSERT INTO descriptor_barcodes (barcode_key, descriptor_id, office_id)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement statement = conn.prepareStatement(sql);) {
            statement.setString(1, barcode);
            statement.setInt(2, descriptor_id);
            statement.setInt(3, office_id);
            int result = statement.executeUpdate();
            if (result == 1) {
                db.executeCommit(conn);
                db.closeConnection(conn);
                return true;
            } else {
                db.executeRollback(conn);
                db.closeConnection(conn);
                throw new DataAccessException("Failed to add barcode to descriptor: 0 or more than 1 row affected");
            }
        } catch (SQLException e) {
            db.executeRollback(conn);
            db.closeConnection(conn);
            throw new DataAccessException("Failed to add barcode to descriptor", e);
        }
    }
}
