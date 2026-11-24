package simple.domain.itemmanagement;

import java.time.OffsetDateTime;

public record Loanable(
        int id,
        String barcode,
        int unitNumber,
        String infoField,
        OffsetDateTime deleted,
        Descriptor descriptor,
        Integer shelfId) {
}
