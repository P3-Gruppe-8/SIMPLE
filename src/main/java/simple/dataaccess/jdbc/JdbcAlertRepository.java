package simple.dataaccess.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.StockAlertMap;
import simple.dataaccess.jdbc.rowMapping.LoanAlertMap;
import simple.domain.alert.AlertOverview;
import simple.domain.alert.LoanAlert;
import simple.domain.alert.StockAlert;
import simple.domain.exception.DataAccessException;
import simple.domain.repository.AlertRepository;

public class JdbcAlertRepository implements AlertRepository {

    @Override
    public AlertOverview findActiveAlertsByOffice(int officeId) {
        Databaseconnection databaseConnection = new Databaseconnection();

        try (Connection connection = databaseConnection.getDbConnection()) {
            ArrayList<StockAlert> stockAlerts = findStockAlerts(connection, officeId);
            ArrayList<LoanAlert> loanAlerts = findLoanAlerts(connection, officeId);

            return new AlertOverview(stockAlerts, loanAlerts);
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Failed to load active alerts for office " + officeId, e);
        }
    }

    private ArrayList<StockAlert> findStockAlerts(Connection connection, int officeId)
            throws SQLException {
        String sql = """
                SELECT stock_alerts.*, descriptors.*
                FROM stock_alerts
                JOIN descriptors
                  ON stock_alerts.descriptor_id = descriptors.descriptor_id
                WHERE descriptors.office_id = ?
                  AND descriptors.descriptor_deleted IS NULL
                  AND stock_alerts.stock_alert_deleted IS NULL
                  AND stock_alerts.raised IS NOT NULL
                  AND descriptors.quantity < stock_alerts.min_quantity
                ORDER BY stock_alerts.urgency
                """;

        ArrayList<StockAlert> stockAlerts = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, officeId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    stockAlerts.add(StockAlertMap.mapRow(result));
                }
            }
        }

        return stockAlerts;
    }

    private ArrayList<LoanAlert> findLoanAlerts(Connection connection, int officeId)
            throws SQLException {
        String sql = """
                SELECT loan_alerts.*, loan_agreements.*, loanables.*, descriptors.*
                FROM loan_alerts
                JOIN loan_agreements
                  ON loan_alerts.loan_id = loan_agreements.loan_id
                JOIN loanables
                  ON loan_agreements.loanable_id = loanables.loanable_id
                JOIN descriptors
                  ON loanables.descriptor_id = descriptors.descriptor_id
                WHERE descriptors.office_id = ?
                  AND descriptors.descriptor_deleted IS NULL
                  AND loanables.loanable_deleted IS NULL
                  AND loan_alerts.loan_alert_deleted IS NULL
                  AND loan_alerts.raised IS NOT NULL
                  AND loan_agreements.actual_return_date IS NULL
                  AND loan_agreements.agreed_return_date < CURRENT_TIMESTAMP
                ORDER BY loan_alerts.urgency
                """;

        ArrayList<LoanAlert> loanAlerts = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, officeId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    loanAlerts.add(LoanAlertMap.mapRow(result));
                }
            }
        }

        return loanAlerts;
    }
}
