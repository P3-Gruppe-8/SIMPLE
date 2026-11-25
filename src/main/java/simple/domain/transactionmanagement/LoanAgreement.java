package simple.domain.transactionmanagement;

import java.time.OffsetDateTime;
import simple.domain.itemmanagement.Loanable;

public record LoanAgreement(
        int id,
        OffsetDateTime loanTime,
        OffsetDateTime agreedReturnDate,
        String borrower,
        String note,
        OffsetDateTime actualReturnDate,
        Loanable loanable) {}