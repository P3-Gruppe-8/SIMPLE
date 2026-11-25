package simple.dataaccess.jdbc.rowMapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import simple.domain.itemmanagement.Loanable;
import simple.domain.transactionmanagement.LoanAgreement;

public class LoanAgreementMap {
    public static LoanAgreement mapRow(ResultSet result) throws SQLException {

        Loanable loanable = LoanableMap.mapRow(result);

        return new LoanAgreement(
                result.getInt("loan_id"),
                result.getObject("loan_time", OffsetDateTime.class),
                result.getObject("agreed_return_date", OffsetDateTime.class),
                result.getString("borrower"),
                result.getString("note"),
                result.getObject("actual_return_date", OffsetDateTime.class),
                loanable);
                
    }
}
