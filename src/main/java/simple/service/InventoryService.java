package simple.service;

import simple.domain.itemmanagement.Inventory;
import simple.domain.repository.InventoryRepository;

public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory getOverviewByOffice(int officeId) {
        return inventoryRepository.getInventoryByOffice(officeId);
    }
}
