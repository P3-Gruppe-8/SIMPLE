package simple.domain.dao;

import java.util.ArrayList;

import simple.domain.alert.LoanAlert;

public interface LoanAlertDAO {
    ArrayList<LoanAlert> getActiveAlerts(int officeId);
}
