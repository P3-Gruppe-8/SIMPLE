package simple.domain.repository;

import java.util.Optional;
import java.util.Date;

import simple.domain.transactionmanagement.LoanAgreement;
import simple.domain.itemmanagement.Loanable;

public interface LoanAgreementRepository {

    public Optional<LoanAgreement> findByLoanable(Loanable loanable);

    public boolean extendLoan(Date date, LoanAgreement loanAgreement);

    public boolean create(LoanAgreement loanAgreement);

    public boolean delete(Loanable loanable);

}
