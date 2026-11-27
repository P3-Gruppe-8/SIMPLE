package simple.service;

import java.util.ArrayList;
import simple.domain.dao.ITStaffDAO;
import simple.domain.employee.ITStaff;
import simple.domain.employee.ITStaffRegistration;

public class ITStaffService {
    private final ITStaffDAO itStaffDAO;

    public ITStaffService(ITStaffDAO itStaffDAO) {
        this.itStaffDAO = itStaffDAO;
    }

    public boolean existsByUsername(String username, int officeId) {
        return itStaffDAO.findByUsername(username, officeId).isPresent();
    }

    public void create(ITStaffRegistration itStaffRegistration) {
        itStaffDAO.create(itStaffRegistration);
    }

    public void delete(int id) {
        itStaffDAO.softDelete(id);
    }

    public ArrayList<ITStaff> getAll(int officeId) {
        return itStaffDAO.findAll(officeId);
    }
}
