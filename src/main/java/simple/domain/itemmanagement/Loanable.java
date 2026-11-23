package simple.domain.itemmanagement;

public record Loanable(
        int id,
        int descriptorId,
        String descriptorName,
        String barcode,
        int unitNumber,
        String infoField,
        Integer shelfId) {}
