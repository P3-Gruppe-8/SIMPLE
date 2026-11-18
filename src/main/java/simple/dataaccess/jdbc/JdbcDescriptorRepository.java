package simple.dataaccess.jdbc;

import java.sql.*;
import simple.dataaccess.config.Databaseconnection;
import simple.domain.exception.DataAccessException;
import simple.domain.repository.DescriptorRepository;
import simple.domain.itemmanagement.Descriptor;

public class JdbcDescriptorRepository implements DescriptorRepository {

    public boolean changeQuantity(int descriptor_id, int change) {

        Databaseconnection db = new Databaseconnection();
        System.out.println(descriptor_id);

        try (Connection conn = db.getDbConnection();
                PreparedStatement decrement = conn
                        .prepareStatement("UPDATE descriptors SET quantity = ? WHERE descriptor_id = ?");) {
            decrement.setInt(1, change);
            decrement.setInt(2, descriptor_id);
            if (decrement.executeUpdate() == 0)
                return false;
            return true;
        } catch (SQLException e) {
            throw new DataAccessException("The database is unresponsive, try again later", e);
        }
    }
}
