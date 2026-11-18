package simple.domain.dao;

import java.util.Optional;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.itemmanagement.Loanable;

public interface LoanableDAO {

    public Optional<Loanable> findByRFID(String barcode);

    public Optional<Loanable> findByUnitName(int unitNumber, Descriptor descriptor);

    public void create(Loanable loanable);

    public void delete(String barcode);
}
