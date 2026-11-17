package simple.domain.dao;

import java.util.List;
import java.util.Optional;

import simple.domain.JDBCException;
import simple.domain.employee.ITStaff;

public interface ITStaffDAO {
    public Optional<ITStaff> findById(int id) throws JDBCException;

    public Optional<ITStaff> findByUsername(String username) throws JDBCException;

    public void delete(int id) throws JDBCException;
}
