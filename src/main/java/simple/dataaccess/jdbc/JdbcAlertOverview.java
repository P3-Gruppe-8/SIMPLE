package simple.dataaccess.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.DescriptorMap;
import simple.dataaccess.jdbc.rowMapping.LoanableMap;
import simple.domain.alert.AlertOverview;
import simple.domain.alert.LoanAlert;
import simple.domain.alert.StockAlert;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Inventory;
import simple.domain.itemmanagement.Loanable;
import simple.domain.repository.AlertRepository;

public class JdbcAlertOverview implements AlertRepository{

    @Override
    public AlertOverview findActiveByOffice(int officeId) {

         ArrayList<StockAlert> stockAlerts = new ArrayList<StockAlert>();
         ArrayList<LoanAlert> loanAlerts = new ArrayList<LoanAlert>();

        String stockAlertsSql = """
                SELECT  stock_alerts.*,
                        descriptors.*,
                FROM stock_alerts
                JOIN descriptors
                  ON stock_alerts.descriptor_id = descriptors.descriptor_id
                 AND descriptors.descriptor_deleted IS NULL
                WHERE descriptors.office_id = ?
                  AND stock_alerts.stock_alert_deleted IS NULL
                  AND descriptors.quantity < stock_alerts.min_quantity
                ORDER BY stock_alerts.urgency
                """;

        Databaseconnection databaseConnection = new Databaseconnection();

        try (Connection connection = databaseConnection.getDbConnection();
                PreparedStatement statement = connection.prepareStatement(Sql)) {
            statement.setInt(1, officeId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    if (!result.getBoolean("is_loanable")) {
                        consumables.add(DescriptorMap.mapRow(result));
                    } else if (result.getObject("loanable_id", Integer.class) != null) {
                        loanables.add(LoanableMap.mapRow(result));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: Failed to load inventory for office " + officeId, e);
        }

         return new AlertOverview(stockAlerts, loanAlerts);


    }
    
}
