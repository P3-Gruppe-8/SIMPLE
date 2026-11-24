package simple.domain.transactionmanagement;

import java.time.OffsetDateTime;

public record LoanAgreementRegistration(OffsetDateTime loan_time, OffsetDateTime agreed_return_date, String borrower,
        String note, String loanable_barcode) {
}
