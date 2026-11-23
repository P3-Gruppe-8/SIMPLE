package simple.service;

import java.time.OffsetDateTime;

public class LoanableService {

    public void receiveLoanable(String barcode) {}

    public void newLoan(OffsetDateTime agreedReturnDate, String borrower, String note, String loanable_barcode) {}
}
