package simple.presentation;

/**
 * JSON body of PATCH /descriptors/{barcode}. The stock changes by {@code change}: negative writes
 * off, positive restocks.
 */
public record DescriptorPatch(int change) {}
