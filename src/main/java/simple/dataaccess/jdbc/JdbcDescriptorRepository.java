package simple.dataaccess.jdbc;

import java.sql.*;
import java.util.Optional;

import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.descriptorMap;
import simple.domain.exception.DataAccessException;
import simple.domain.repository.DescriptorRepository;
import simple.domain.itemmanagement.Descriptor;

public class JdbcDescriptorRepository implements DescriptorRepository {

    public boolean changeQuantity(int descriptor_id, int change) {

        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection();
                PreparedStatement decrement = conn
                        .prepareStatement("UPDATE descriptors SET quantity = ? WHERE descriptor_id = ?");) {
            decrement.setInt(1, change);
            decrement.setInt(2, descriptor_id);
            if (decrement.executeUpdate() == 0)
                return false;
            return true;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to change the descriptors quantity: Unresponsive database", e);
        }
    }

    public Optional<Descriptor> findByBarcode(String barcode, int office_id) {
        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection();
                PreparedStatement statement = conn
                        .prepareStatement("""
                                        SELECT d.*
                                        FROM descriptors d
                                        JOIN descriptor_barcodes db
                                            ON d.descriptor_id = db.descriptor_id
                                            AND d.office_id = db.office_id
                                        WHERE db.barcode_key = ?
                                            AND db.office_id = ?;
                                        """);) {
            statement.setString(1, barcode);
            statement.setInt(2, office_id);
            try (ResultSet result = statement.executeQuery();) {
                return result.next() ? Optional.of(descriptorMap.mapRow(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find descriptor by barcode: Unresponsive database", e);
        }
    }
}

