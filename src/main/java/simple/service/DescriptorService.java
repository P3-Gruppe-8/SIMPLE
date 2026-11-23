package simple.service;

import java.util.Optional;

import simple.dataaccess.jdbc.JdbcDescriptorRepository;
import simple.domain.itemmanagement.Descriptor;

public class DescriptorService {
    public void writeOff(String barcode, int office_id, int change) {
        JdbcDescriptorRepository jdbc = new JdbcDescriptorRepository();
        Optional<Descriptor> opt_descriptor = jdbc.changeQuantity(barcode, office_id, -change);
    }

    public void deregister(String barcode, int office_id) {
        JdbcDescriptorRepository jdbc = new JdbcDescriptorRepository();
        Optional<Descriptor> opt_descriptor = jdbc.findByBarcode(barcode, office_id);

        if (opt_descriptor.isPresent()) {
            Descriptor descriptor = opt_descriptor.get();
            boolean result = jdbc.softDelete(descriptor.id(), descriptor.office_id());
        }
    }

    public void register(String descriptor_name, boolean is_loanable, String info_field, int office_id, String barcode,
            int shelf_id) {
        JdbcDescriptorRepository jdbc = new JdbcDescriptorRepository();
        boolean result = jdbc.createNew(descriptor_name, is_loanable, info_field, office_id, barcode, shelf_id);
    }

    public void restock(String barcode, int office_id, int change) {
        JdbcDescriptorRepository jdbc = new JdbcDescriptorRepository();
        Optional<Descriptor> opt_descriptor = jdbc.changeQuantity(barcode, office_id, change);
    }

    public void getItemDetails(String barcode) {
    }

    public void removeShelf(String barcode, int shelf_id) {
    }

    public void addShelf(String barcode, int shelf_id) {
    }
}
