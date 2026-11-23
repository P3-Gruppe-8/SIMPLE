package simple.presentation;

/** JSON body of POST /descriptors, as sent by the register dialog form. */
public record DescriptorRegistration(String name, String barcode, String shelf) {}
