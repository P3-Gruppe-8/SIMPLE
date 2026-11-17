package simple.domain.dao;

import java.util.List;
import java.util.Optional;

import simple.domain.JDBCException;
import simple.domain.itemmanagement.Office;

public interface OfficeDAO {
    public Optional<Office> findById(int id) throws JDBCException;

    public Optional<Office> findByName(String name) throws JDBCException;

    public List<Office> findAll() throws JDBCException;

    public void update(Office office) throws JDBCException;

    public void delete(int id) throws JDBCException;
}
