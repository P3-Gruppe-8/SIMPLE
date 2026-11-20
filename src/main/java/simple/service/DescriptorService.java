package simple.service;

public class DescriptorService {

    public void writeOff(String barcode, int change) {}

    public void deregister(String barcode) {}

    public void register(String barcode) {}

    public void restock(String barcode, int amount) {}

    public void getItemDetails(String barcode) {}

    public void removeShelf(String barcode, int shelf_id) {}

    public void addShelf(String barcode, int shelf_id) {}
}
