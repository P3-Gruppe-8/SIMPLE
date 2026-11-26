package simple.domain.dao;

import java.util.List;
import simple.domain.itemmanagement.Shelf;

public interface ShelfDAO {

    // public Optional<Shelf> findById(int id);

    // public Optional<Shelf> findByName(String name);

    // public List<String> findBarcodes(Shelf shelf);

    public List<Shelf> findAllByOffice(int officeId);

    // public void update(Shelf shelf);

    // public void delete(String barcode);
}
