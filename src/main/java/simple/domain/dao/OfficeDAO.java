package simple.domain.dao;

import java.util.List;
import java.util.Optional;

import simple.domain.itemmanagement.Office;

public interface OfficeDAO {
    public Optional<Office> findById(int id);

    public Optional<Office> findByName(String name);

    public List<Office> findAll();

    public void update(Office office);

    public void delete(int id);
}
