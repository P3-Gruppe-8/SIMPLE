package simple.presentation;

/** JSON body of GET /descriptors/{barcode}. */
public record DescriptorDetails(String barcode, String name, int quantity) {}
