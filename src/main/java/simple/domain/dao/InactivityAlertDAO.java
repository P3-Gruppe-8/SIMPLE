package simple.domain.dao;

import java.util.List;

import simple.domain.JDBCException;
import simple.domain.alert.InactivityAlert;

public interface InactivityAlertDAO {
    public List<InactivityAlert> findAll() throws JDBCException;
}
