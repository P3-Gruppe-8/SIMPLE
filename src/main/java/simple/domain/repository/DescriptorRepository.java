package simple.domain.repository;

import java.util.Optional;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Descriptor;

public interface DescriptorRepository {

    /*
     * public Optional<Descriptor> findById(int id);
     *
     * public List<Shelf> findPossibleShelves(int descriptorId);
     *
     * public Optional<Descriptor> findByName(String name);
     *
     * public List<Descriptor> findAll();
     */




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
    public Optional<Descriptor> changeQuantity(String barcode, int officeId, int delta);

    public Optional<Descriptor> findByBarcode(String barcode, int office_id);

    public boolean softDelete(int descriptor_id, int office_id);

    public boolean createNew(String descriptor_name, boolean is_loanable, String info_field, int office_id);
}
