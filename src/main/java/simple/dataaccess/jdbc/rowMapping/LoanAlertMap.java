package simple.dataaccess.jdbc.rowMapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import simple.domain.alert.LoanAlert;
import simple.domain.alert.Urgency;
import simple.domain.transactionmanagement.LoanAgreement;

public class LoanAlertMap {
    public static LoanAlert mapRow(ResultSet result) throws SQLException {

        LoanAgreement loanAgreement = LoanAgreementMap.mapRow(result);

        return new LoanAlert(
                result.getInt("loan_alert_id"),
                Urgency.valueOf(result.getString("urgency")),
                result.getObject("raised", OffsetDateTime.class) != null,
                result.getBoolean("muted"),
                loanAgreement);
    }
}
