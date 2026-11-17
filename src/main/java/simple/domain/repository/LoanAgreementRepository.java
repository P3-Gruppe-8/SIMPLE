package simple.domain.repository;

import java.util.Date;
import java.util.Optional;

import simple.domain.transactionmanagement.LoanAgreement;
import simple.domain.itemmanagement.Loanable;

public interface LoanAgreementRepository {

    public Optional<LoanAgreement> findByLoanable(Loanable loanable);

    public void extendLoan(Date date, LoanAgreement loanAgreement);

    public void create(LoanAgreement loanAgreement);

    public void delete(Loanable loanable);
}
