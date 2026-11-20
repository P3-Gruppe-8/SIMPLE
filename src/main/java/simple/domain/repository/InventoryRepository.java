package simple.domain.repository;

import java.util.ArrayList;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.itemmanagement.Loanable;

public interface InventoryRepository {

    record InventoryOverview(ArrayList<Descriptor> consumables, ArrayList<Loanable> loanables) {}

    public InventoryOverview findOverviewByOffice(int officeId);
}
