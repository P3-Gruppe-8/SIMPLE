package simple.service;

import simple.domain.dao.ITStaffDAO;

public class ITStaffService {
    private final ITStaffDAO itStaffDAO;

    public ITStaffService(ITStaffDAO itStaffDAO) {
        this.itStaffDAO = itStaffDAO;
    }

    public boolean existsByUsername(String username, int officeId) {
        return itStaffDAO.findByUsername(username, officeId).isPresent();
    }
}
