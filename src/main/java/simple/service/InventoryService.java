package simple.service;

import simple.domain.repository.InventoryRepository;
import simple.domain.itemmanagement.Inventory;

public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory getOverviewByOffice(int officeId) {
        return inventoryRepository.findOverviewByOffice(officeId);
    }
}
