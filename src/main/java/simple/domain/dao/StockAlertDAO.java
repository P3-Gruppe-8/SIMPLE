package simple.domain.dao;

import java.util.ArrayList;
import simple.domain.alert.StockAlert;

public interface StockAlertDAO {
    ArrayList<StockAlert> getActiveAlerts(int officeId);
}
