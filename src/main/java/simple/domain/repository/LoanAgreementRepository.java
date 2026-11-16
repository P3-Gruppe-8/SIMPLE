package simple.domain.repository;

import java.util.Date;
import java.util.Optional;
import simple.domain.itemmanagement.Loanable;
import simple.domain.transactionmanagement.LoanAgreement;

public interface LoanAgreementRepository {

    public Optional<LoanAgreement> findByLoanable(Loanable loanable);

    public boolean extendLoan(Date date, LoanAgreement loanAgreement);

    public boolean create(LoanAgreement loanAgreement);

    public boolean delete(Loanable loanable);
}
