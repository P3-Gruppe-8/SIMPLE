package simple.domain.repository;

import java.util.List;
import java.util.Optional;

import simple.domain.JDBCException;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.itemmanagement.Office;
import simple.domain.itemmanagement.Shelf;

public interface DescriptorRepository {

    public Optional<Descriptor> findById(int id) throws JDBCException;

    public List<Shelf> findPosibleShelves(int descriptorId) throws JDBCException;

    public Optional<Descriptor> findByBarcode(String barcode, Office office) throws JDBCException;

    public Optional<Descriptor> findByName(String name) throws JDBCException;

    public List<Descriptor> findAll() throws JDBCException;

    public void create(Descriptor descriptor) throws JDBCException;

    public void delete(String barcode) throws JDBCException;
}
