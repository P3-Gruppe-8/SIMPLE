package simple.domain.itemmanagement;

import java.util.ArrayList;

public record Inventory (ArrayList<Descriptor> consumables, ArrayList<Loanable> loanables) {}