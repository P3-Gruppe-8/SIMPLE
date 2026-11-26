package simple.domain.alert;

import simple.domain.itemmanagement.Descriptor;

public record StockAlert(
        int id, Urgency urgency, boolean active, boolean muted, int minQuantity, Descriptor descriptor) {}
