package simple.domain.dao;

import java.util.ArrayList;
import java.util.Optional;
import simple.domain.itemmanagement.Loanable;
import simple.domain.itemmanagement.LoanableRegistration;

public interface LoanableDAO {

    public Optional<Loanable> findByBarcode(String barcode);

    public ArrayList<Loanable> findAllByDescriptor(int descriptorId);

    public void create(LoanableRegistration loanableRegistration, int unitNumber);

    /*
     * public Optional<Loanable> findByUnitName(int unitNumber, Descriptor
     * descriptor);
     *
     * public void delete(String barcode);
     */
}
