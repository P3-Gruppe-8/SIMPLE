package simple.dataaccess.jdbc;

import java.sql.*;
import java.util.Optional;
import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.LoanableMap;
import simple.domain.dao.LoanableDAO;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Loanable;

public class JdbcLoanableDAO implements LoanableDAO {

    public Optional<Loanable> findByBarcode(String barcode) {
        String sql = """
                SELECT loanables.*, descriptors.*
                FROM loanables
                JOIN descriptors ON descriptors.descriptor_id = loanables.descriptor_id
                WHERE loanables.loanable_barcode = ?
                loanables.loanable_deleted IS NULL
                """;

        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection();
                PreparedStatement statement = conn.prepareStatement(sql); ) {
            statement.setString(1, barcode);
            try (ResultSet result = statement.executeQuery(); ) {
                return result.next() ? Optional.of(LoanableMap.mapRow(result)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find loanable by barcode: Unresponsive database", e);
        }
    }
}
