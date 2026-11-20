package simple.domain.repository;

import java.util.List;
import java.util.Optional;

import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.itemmanagement.Office;
import simple.domain.itemmanagement.Shelf;

public interface DescriptorRepository {

    /*
     * public Optional<Descriptor> findById(int id);
     *
     * public List<Shelf> findPosibleShelves(int descriptorId);
     *
     * public Optional<Descriptor> findByName(String name);
     *
     * public List<Descriptor> findAll();
     *
     * public void create(Descriptor descriptor);
     *
     * public void delete(String barcode);
     */
    public Optional<Descriptor> findByBarcode(String barcode, int office_id);

/**
 * Adjusts the stored stock quantity by the given amount.
 *
 * @param descriptor_id the ID of the descriptor to update
 * @param change the quantity adjustment, positive for restocking
 *               and negative for write-offs
 * @return {@code true} if a descriptor row was updated,
 *         or {@code false} if no descriptor matched the ID
 * @throws DataAccessException if the database operation fails,
 *         including when the resulting quantity would be negative
 */
    public boolean changeQuantity(int descriptor_id, int change);
}
