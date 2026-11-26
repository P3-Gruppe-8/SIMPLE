package simple.service;

import java.util.Optional;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.repository.DescriptorRepository;

public class DescriptorService {
    private final DescriptorRepository descriptorRepository;

    public DescriptorService(DescriptorRepository descriptorRepository) {
        this.descriptorRepository = descriptorRepository;
    }

    public Optional<Descriptor> findByBarcode(String barcode, int officeId) {
        return descriptorRepository.findByBarcode(barcode, officeId);
    }

    public Optional<Descriptor> writeOff(String barcode, int officeId, int amount) {
        return descriptorRepository.changeQuantity(barcode, officeId, -amount);
    }

    public Optional<Descriptor> restock(String barcode, int officeId, int amount) {
        return descriptorRepository.changeQuantity(barcode, officeId, amount);
    }

    public Optional<Descriptor> deregister(String barcode, int officeId) {
        Optional<Descriptor> descriptor = descriptorRepository.findByBarcode(barcode, officeId);
        if (descriptor.isPresent()) {
            descriptorRepository.softDelete(descriptor.get().id(), officeId);
        }
        return descriptor;
    }

    public void register(
            String descriptorName, boolean isLoanable, String infoField, int officeId, String barcode, int shelfId) {
        descriptorRepository.createNew(descriptorName, isLoanable, infoField, officeId, barcode, shelfId);
    }

    public void getItemDetails(String barcode) {}

    public void removeShelf(String barcode, int shelf_id) {}

    public void addShelf(String barcode, int shelf_id) {}
}
