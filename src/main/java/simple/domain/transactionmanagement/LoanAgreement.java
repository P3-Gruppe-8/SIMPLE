package simple.domain.transactionmanagement;

import java.time.OffsetDateTime;

public record LoanAgreement(
        int id,
        OffsetDateTime loan_time,
        OffsetDateTime agreed_return_date,
        String borrower,
        String note,
        OffsetDateTime actual_return_date,
        int loanable_id) {
}
