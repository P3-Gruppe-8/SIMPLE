package simple.domain.dao;

import java.util.ArrayList;
import java.util.Optional;
import simple.domain.employee.ITStaff;
import simple.domain.employee.ITStaffRegistration;

public interface ITStaffDAO {

    public void create(ITStaffRegistration itStaffRegistration);

    public Optional<ITStaff> findByUsername(String username, int officeId);

    public void softDelete(int id);

    public ArrayList<ITStaff> findAll(int officeId);
}
