package simple.domain.repository;

import java.util.List;
import simple.domain.alert.StockAlert;
import simple.domain.itemmanagement.Descriptor;

public interface StockAlertRepository {

    public List<StockAlert> findAll();

    public boolean create(StockAlert stockAlert);

    public boolean delete(Descriptor descriptor);
}
