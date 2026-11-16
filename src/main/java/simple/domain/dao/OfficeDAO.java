package simple.domain.dao;

import simple.domain.itemmanagement.*;
import java.util.Optional;
import java.util.List;

public interface OfficeDAO {
    public Optional<Office> findById(int id);

    public Optional<Office> findByName(String name);

    public Optional<Office> findByAddress(String address);

    public List<Office> findAll();

    public boolean update(Office office);

    public boolean delete(int id);
}
