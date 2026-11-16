package simple.domain.dao;

import java.util.List;
import java.util.Optional;

import simple.domain.employee.ITStaff;

public interface ITStaffDAO {
    public Optional<ITStaff> findById(int id);

    public Optional<ITStaff> findByUsername(String username);

    public Optional<ITStaff> findByFullName(String fullname);

    public List<ITStaff> findAll();

    public boolean update(ITStaff itstaff);

    public boolean delete(int id);
}
