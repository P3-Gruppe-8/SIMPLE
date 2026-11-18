package simple.domain.dao;

import java.util.Optional;
import simple.domain.employee.Admin;

public interface AdminDAO {
    public Optional<Admin> findById(int id);
}
