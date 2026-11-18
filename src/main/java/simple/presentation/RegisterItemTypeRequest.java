package simple.presentation;

/** JSON body of POST /registerItemType, as sent by the register dialog form. */
public record RegisterItemTypeRequest(String name, String itemId, String shelf) {}
