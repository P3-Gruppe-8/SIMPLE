package simple;

import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.JdbcInventory;
import simple.dataaccess.jdbc.JdbcITStaffDAO;
import simple.domain.itemmanagement.Inventory;
import simple.presentation.Server;
import simple.service.ITStaffService;
import simple.service.InventoryService;

public class Main {
    public static void main(String[] args) {

        Databaseconnection db = new Databaseconnection();

        // Applies pending database migrations before starting the server.
        db.migrate();

        // Test af existsByUsername:

        ITStaffService itStaffService = new ITStaffService(new JdbcITStaffDAO());
        System.out.println(itStaffService.existsByUsername("demo.anna", 1));
        System.out.println(itStaffService.existsByUsername("demo.victor", 1));

        // Test af getOverviewByOffice
        // InventoryService inventoryService = new InventoryService(new
        // JdbcInventory());

        // Inventory inventory = inventoryService.getOverviewByOffice(1);

        // System.out.println("Consumables:");
        // inventory.consumables().forEach(System.out::println);

        // System.out.println("Loanables:");
        // inventory.loanables().forEach(System.out::println);

        // Tests af findByBarcode og changeQuantity på descriptor

        // JdbcDescriptorRepository jdbcDescriptorRepository = new
        // JdbcDescriptorRepository();

        // Optional<Descriptor> descriptor1 = jdbcDescriptorRepository
        // .findByBarcode("DEMO-USB-C-1M", 1);

        // System.out.println("Quantaty før ændring: " +
        // descriptor1.orElseThrow().quantity());

        // descriptor1 = jdbcDescriptorRepository.changeQuantity("DEMO-USB-C-1M", 1, 1);

        // System.out.println("Quantaty efter ændring: " +
        // descriptor1.orElseThrow().quantity());

        // Use port from environment variables if available,
        // Azure supplies this port because it is what it opens
        String portValue = System.getenv("PORT");
        int port = (portValue == null || portValue.isBlank()) ? 7070 : Integer.parseInt(portValue);

        Server.start(port);
    }
}
