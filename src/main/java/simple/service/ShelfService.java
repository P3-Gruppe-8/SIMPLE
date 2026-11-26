package simple.service;

import java.util.List;
import simple.domain.dao.ShelfDAO;
import simple.domain.itemmanagement.Shelf;

public class ShelfService {
    private final ShelfDAO shelfDAO;

    public ShelfService(ShelfDAO shelfDAO) {
        this.shelfDAO = shelfDAO;
    }

    public List<Shelf> getAllByOffice(int officeId) {
        return shelfDAO.findAllByOffice(officeId);
    }
}
