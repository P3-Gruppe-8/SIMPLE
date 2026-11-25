package simple.dataaccess.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.LoanAlertMap;
import simple.domain.alert.LoanAlert;
import simple.domain.exception.DataAccessException;
import simple.domain.dao.LoanAlertDAO;

public class JdbcLoanAlertDAO implements LoanAlertDAO {

  @Override
  public ArrayList<LoanAlert> getActiveAlerts(int officeId) {
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

    try (Connection connection = new Databaseconnection().getDbConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, officeId);

      try (ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          loanAlerts.add(LoanAlertMap.mapRow(result));
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to load loan alerts for office " + officeId, e);
    }

    return loanAlerts;
  }
}
