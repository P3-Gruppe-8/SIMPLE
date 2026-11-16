package simple.domain.dao;

import java.util.Optional;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.itemmanagement.Loanable;

public interface LoanableDAO {

    public Optional<Loanable> findById(int id);

    public Optional<Loanable> findByRFID(String RFID);

    public Optional<Loanable> findByUnitName(int unitNumber, Descriptor descriptor);

    public boolean create(Loanable loanable);

    public boolean delete(String barcode);
}
