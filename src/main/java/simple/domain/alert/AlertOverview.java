package simple.domain.alert;

import java.util.ArrayList;

public record AlertOverview(ArrayList<StockAlert> stockAlerts, ArrayList<LoanAlert> loanAlerts) {}
