package simple.domain.repository;

import java.util.List;

import simple.domain.JDBCException;
import simple.domain.alert.StockAlert;
import simple.domain.itemmanagement.Descriptor;

public interface StockAlertRepository {

    public List<StockAlert> findAll() throws JDBCException;

    public void create(StockAlert stockAlert) throws JDBCException;

    public void delete(Descriptor descriptor) throws JDBCException;
}
