package simple.service;
import java.util.Optional;

import simple.dataaccess.jdbc.JdbcDescriptorRepository;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.exception.databaseUpdateException;

public class DescriptorService {

    public void writeOff(String barcode, int office_id, int change) {
        JdbcDescriptorRepository jdbc = new JdbcDescriptorRepository();
        jdbc.changeQuantity(barcode, office_id, change);
    }

    public void deregister(String barcode, int office_id) {
        JdbcDescriptorRepository jdbc = new JdbcDescriptorRepository();
        Optional<Descriptor> optional = jdbc.findByBarcode(barcode, office_id);

        if (optional.isPresent()) {
            Descriptor descriptor = optional.get();
            boolean result = jdbc.softDelete(descriptor.id(), descriptor.office_id());

            if (!result) {
                throw new databaseUpdateException("Failed to delete descriptor: 0 rows affected");
            }
        }
        else {
            throw new databaseUpdateException("Failed to delete descriptor: Empty Optional");
        }
    }

    public void register(String barcode) {

    }

    public void restock(String barcode, int amount) {

    }

    public void getItemDetails(String barcode) {}

    public void removeShelf(String barcode, int shelf_id) {}

    public void addShelf(String barcode, int shelf_id) {}
}