package simple.domain.repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import simple.domain.transactionmanagement.LoanAgreement;
import simple.domain.transactionmanagement.LoanAgreementRegistration;

public interface LoanAgreementRepository {
    /*
     * public Optional<LoanAgreement> findByLoanable(Loanable loanable);
     *
     * public void extendLoan(Date date, LoanAgreement loanAgreement);
     *
     * public void create(LoanAgreement loanAgreement);
     *
     * public void delete(Loanable loanable);
     */

    public void softDelete(String barcode);

    public void create(LoanAgreementRegistration loanAgreementRegistration, int loanable_id);

    public void changeReturnDate(OffsetDateTime newDate, int loanAgreementId);

    public ArrayList<LoanAgreement> findAll(int officeId);
}
