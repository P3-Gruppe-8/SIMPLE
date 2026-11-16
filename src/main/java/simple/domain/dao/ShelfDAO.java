package simple.domain.dao;

import java.util.List;
import java.util.Optional;
import simple.domain.itemmanagement.Shelf;

public interface ShelfDAO {

    public Optional<Shelf> findById(int id);

    public Optional<Shelf> findByName(String name);

    public List<String> findBarcodes(Shelf shelf);

    public List<Shelf> findAll();

    public boolean update(Shelf shelf);

    public boolean delete(String barcode);
}
