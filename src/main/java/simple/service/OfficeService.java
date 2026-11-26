package simple.service;

import java.util.List;
import simple.domain.dao.OfficeDAO;
import simple.domain.itemmanagement.Office;

public class OfficeService {
    private final OfficeDAO officeDAO;

    public OfficeService(OfficeDAO officeDAO) {
        this.officeDAO = officeDAO;
    }

    public List<Office> getAll() {
        return officeDAO.findAll();
    }
}
