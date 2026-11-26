package simple.presentation;

/** JSON body of POST /descriptors/{barcode}/restock and /write-off. */
public record StockChange(int amount) {}
