package simple.dataaccess.jdbc;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.DescriptorMap;
import simple.dataaccess.jdbc.rowMapping.LoanableMap;
import simple.domain.exception.AffectedRowsException;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Loanable;
import simple.domain.repository.LoanAgreementRepository;
import simple.domain.transactionmanagement.LoanAgreementRegistration;
import simple.domain.transactionmanagement.LoanAgreement;
import simple.dataaccess.jdbc.rowMapping.LoanAgreementMap;

public class JdbcLoanAgreementRepository implements LoanAgreementRepository {
    public void softDelete(String barcode) {

        String sql = """
                UPDATE loan_agreements
                SET actual_return_date = ?
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
                statement.setObject(1, OffsetDateTime.now());
                statement.setString(2, barcode);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected != 1) {
                    throw new AffectedRowsException("Expected 1 row affected, but was " + rowsAffected);
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
                INSERT INTO loan_agreements (loan_time, agreed_return_date, borrower, note, loanable_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setObject(1, OffsetDateTime.now());
                statement.setObject(2, loanAgreementRegistration.agreed_return_date());
                statement.setString(3, loanAgreementRegistration.borrower());
                statement.setString(4, loanAgreementRegistration.note());
                statement.setInt(5, loanable_id);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected != 1) {
                    throw new AffectedRowsException("Expected 1 row affected, but was " + rowsAffected);
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

    public void changeReturnDate(OffsetDateTime newDate, int loanAgreementId) {

        String sql = """
                UPDATE loan_agreements
                SET agreed_return_date = ?
                WHERE loan_id = ?
                AND actual_return_date IS NULL
                """;

        Databaseconnection db = new Databaseconnection();

        try (Connection conn = db.getDbConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setObject(1, newDate);
                statement.setInt(2, loanAgreementId);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected != 1) {
                    throw new AffectedRowsException("Expected 1 row affected, but was " + rowsAffected);
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

    public ArrayList<LoanAgreement> findAll(int officeId) {

        ArrayList<LoanAgreement> loanAgreements = new ArrayList<LoanAgreement>();

        String sql = """
                SELECT loan_agreements.*, loanables.*, descriptors.*
                FROM loan_agreements
                JOIN loanables ON loan_agreements.loanable_id = loanables.loanable_id
                JOIN descriptors ON loanables.descriptor_id = descriptors.descriptor_id
                WHERE loan_agreements.actual_return_date IS NULL
                AND descriptors.office_id = ?
                """;

        Databaseconnection databaseConnection = new Databaseconnection();

        try (Connection connection = databaseConnection.getDbConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, officeId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    loanAgreements.add(LoanAgreementMap.mapRow(result));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: Failed to retrieve loan agreements for office " + officeId,
                    e);
        }
        return loanAgreements;
    }
}
