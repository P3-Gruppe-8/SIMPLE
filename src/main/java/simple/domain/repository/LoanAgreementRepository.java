package simple.domain.repository;

import java.util.Date;
import java.util.Optional;

import simple.domain.transactionmanagement.LoanAgreement;
import simple.domain.JDBCException;
import simple.domain.itemmanagement.Loanable;

public interface LoanAgreementRepository {

    public Optional<LoanAgreement> findByLoanable(Loanable loanable) throws JDBCException;

    public void extendLoan(Date date, LoanAgreement loanAgreement) throws JDBCException;

    public void create(LoanAgreement loanAgreement) throws JDBCException;

    public void delete(Loanable loanable) throws JDBCException;
}
