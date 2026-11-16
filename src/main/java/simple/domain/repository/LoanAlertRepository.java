package simple.domain.repository;

import java.util.List;

import simple.domain.alert.LoanAlert;
import simple.domain.transactionmanagement.LoanAgreement;

public interface LoanAlertRepository {
    public List<LoanAlert> findAll();

    public boolean create(LoanAlert loanAlert);

    public boolean delete(LoanAgreement loanAgreement);
}
