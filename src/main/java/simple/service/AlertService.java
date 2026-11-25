package simple.service;

import java.util.ArrayList;

import simple.domain.alert.AlertOverview;
import simple.domain.alert.LoanAlert;
import simple.domain.alert.StockAlert;
import simple.domain.dao.StockAlertDAO;
import simple.domain.dao.LoanAlertDAO;

public class AlertService {
    private final StockAlertDAO stockAlertDAO;
    private final LoanAlertDAO loanAlertDAO;

    public AlertService(StockAlertDAO stockAlertDAO, LoanAlertDAO loanAlertDAO) {
        this.stockAlertDAO = stockAlertDAO;
        this.loanAlertDAO = loanAlertDAO;
    }

    public AlertOverview getActiveByOfficeId(int officeId) {
        ArrayList<StockAlert> stockAlerts = stockAlertDAO.getActiveAlerts(officeId);
        ArrayList<LoanAlert> loanAlerts = loanAlertDAO.getActiveAlerts(officeId);
        return new AlertOverview(stockAlerts, loanAlerts);
    }
}
