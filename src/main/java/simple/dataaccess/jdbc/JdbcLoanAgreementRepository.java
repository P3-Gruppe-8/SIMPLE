package simple.dataaccess.jdbc;

import simple.domain.repository.LoanAgreementRepository;
import simple.domain.transactionmanagement.LoanAgreementRegistration;

import java.sql.*;
import java.util.Optional;

import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.LoanableMap;
import simple.domain.exception.AffectedRowsException;
import simple.domain.exception.DataAccessException;

public class JdbcLoanAgreementRepository implements LoanAgreementRepository {
    public void softDelete(String barcode) {

        String sql = """
                UPDATE loan_agreements
                SET actual_return_date = now()
                FROM loanables
                WHERE loan_agreements.loanable_id = loanables.loanable_id
                  AND loanables.loanable_barcode = ?
                  AND loanables.loanable_deleted IS NULL
                  AND loan_agreements.actual_return_date IS NULL
                """;

        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, barcode);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected != 1) {
                    throw new AffectedRowsException(
                            "Expected 1 row affected, but was " + rowsAffected);
                }

                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to change return date", e);
        }

    }

    public void create(LoanAgreementRegistration loanAgreementRegistration, int loanable_id) {

        String sql = """
                INSERT INTO loan_agreement (loan_time, agreed_return_date, borrower, note, loanable_id)
                VALUES (now(), ?, ?, ?, ?)
                """;

        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setObject(1, loanAgreementRegistration.agreed_return_date());
                statement.setString(2, loanAgreementRegistration.borrower());
                statement.setString(3, loanAgreementRegistration.note());
                statement.setInt(4, loanable_id);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected != 1) {
                    throw new AffectedRowsException(
                            "Expected 1 row affected, but was " + rowsAffected);
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create loan agreement: Unresponsive database", e);
        }

    }

}
