package simple.dataaccess.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.StockAlertMap;
import simple.domain.alert.StockAlert;
import simple.domain.dao.StockAlertDAO;
import simple.domain.exception.DataAccessException;

public class JdbcStockAlertDAO implements StockAlertDAO {

    @Override
    public ArrayList<StockAlert> getActiveAlerts(int officeId) {
        String sql = """
                SELECT stock_alerts.*, descriptors.*
                FROM stock_alerts
                JOIN descriptors
                  ON stock_alerts.descriptor_id = descriptors.descriptor_id
                WHERE descriptors.office_id = ?
                  AND descriptors.descriptor_deleted IS NULL
                  AND stock_alerts.stock_alert_deleted IS NULL
                  AND stock_alerts.raised IS NOT NULL
                  AND descriptors.quantity <= stock_alerts.min_quantity
                ORDER BY stock_alerts.urgency
                """;

        ArrayList<StockAlert> stockAlerts = new ArrayList<>();

        try (Connection connection = new Databaseconnection().getDbConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, officeId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    stockAlerts.add(StockAlertMap.mapRow(result));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load stock alerts for office " + officeId, e);
        }

        return stockAlerts;
    }
}
