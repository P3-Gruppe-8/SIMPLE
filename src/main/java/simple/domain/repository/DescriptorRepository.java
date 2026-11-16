package simple.domain.repository;

import java.util.List;
import java.util.Optional;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.itemmanagement.Office;
import simple.domain.itemmanagement.Shelf;

public interface DescriptorRepository {

    public Optional<Descriptor> findById(int id);

    public List<Shelf> findPosibleShelves(int descriptorId);

    public Optional<Descriptor> findByBarcode(String barcode, Office office);

    public Optional<Descriptor> findByName(String name);

    public List<Descriptor> findAll();

    public boolean create(Descriptor descriptor);

    public boolean delete(String barcode);
}
