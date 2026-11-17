package simple.domain.dao;

import java.util.Optional;

import simple.domain.employee.ITStaff;

public interface ITStaffDAO {
    public Optional<ITStaff> findById(int id);

    public Optional<ITStaff> findByUsername(String username);

    public void delete(int id);
}
