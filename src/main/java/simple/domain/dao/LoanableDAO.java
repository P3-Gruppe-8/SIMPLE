package simple.domain.dao;

import java.util.Optional;

import simple.domain.JDBCException;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.itemmanagement.Loanable;

public interface LoanableDAO {

    public Optional<Loanable> findByRFID(String barcode) throws JDBCException;

    public Optional<Loanable> findByUnitName(int unitNumber, Descriptor descriptor) throws JDBCException;

    public void create(Loanable loanable) throws JDBCException;

    public void delete(String barcode) throws JDBCException;
}
