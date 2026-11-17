package simple.domain.dao;

import java.util.List;
import java.util.Optional;

import simple.domain.JDBCException;
import simple.domain.itemmanagement.Shelf;

public interface ShelfDAO {

    public Optional<Shelf> findById(int id) throws JDBCException;

    public Optional<Shelf> findByName(String name) throws JDBCException;

    public List<String> findBarcodes(Shelf shelf) throws JDBCException;

    public List<Shelf> findAll() throws JDBCException;

    public void update(Shelf shelf) throws JDBCException;

    public void delete(String barcode) throws JDBCException;
}
