package simple.service;

import simple.domain.repository.InventoryRepository;
import simple.domain.repository.InventoryRepository.InventoryOverview;

public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryOverview getOverviewByOffice(int officeId) {
        return inventoryRepository.findOverviewByOffice(officeId);
    }
}
