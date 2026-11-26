package simple.domain.repository;

import simple.domain.itemmanagement.Inventory;

public interface InventoryRepository {

    public Inventory getInventoryByOffice(int officeId);
}
