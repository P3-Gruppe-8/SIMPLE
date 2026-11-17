package simple.domain.repository;

import java.util.List;

import simple.domain.JDBCException;
import simple.domain.alert.LoanAlert;
import simple.domain.transactionmanagement.LoanAgreement;

public interface LoanAlertRepository {
    public List<LoanAlert> findAll() throws JDBCException;

    public void create(LoanAlert loanAlert) throws JDBCException;

    public void delete(LoanAgreement loanAgreement) throws JDBCException;
}
