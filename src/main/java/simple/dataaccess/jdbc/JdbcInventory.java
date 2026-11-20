package simple.dataaccess.jdbc;

import java.sql.*;
import java.util.ArrayList;
import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.descriptorMap;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.itemmanagement.Loanable;
import simple.domain.itemmanagement.Inventory;
import simple.domain.repository.InventoryRepository;

public class JdbcInventory implements InventoryRepository {

    @Override
    public Inventory findOverviewByOffice(int officeId) {

        ArrayList<Descriptor> consumables = new ArrayList<Descriptor>();
        ArrayList<Loanable> loanables = new ArrayList<Loanable>();

        String consumablesSql = """
                SELECT *
                FROM descriptors
                WHERE office_id = ?
                  AND is_loanable = false
                  AND descriptor_deleted IS NULL
                ORDER BY descriptor_name, descriptor_id
                """;

        String loanablesSql = """
                SELECT loanables.loanable_id,
                       loanables.descriptor_id,
                       descriptors.descriptor_name,
                       loanables.loanable_barcode,
                       loanables.unit_number,
                       loanables.info_field,
                       loanables.shelf_id
                FROM loanables
                JOIN descriptors ON descriptors.descriptor_id = loanables.descriptor_id
                WHERE descriptors.office_id = ?
                  AND descriptors.is_loanable = true
                  AND loanables.loanable_deleted IS NULL
                  AND descriptors.descriptor_deleted IS NULL
                ORDER BY descriptors.descriptor_name, descriptors.descriptor_id, loanables.unit_number
                """;

        
        Databaseconnection databaseConnection = new Databaseconnection();

        try (Connection connection = databaseConnection.getDbConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(consumablesSql)) {
                statement.setInt(1, officeId);

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        consumables.add(descriptorMap.mapRow(result));
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(loanablesSql)) {
                statement.setInt(1, officeId);

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        loanables.add(new Loanable(
                                result.getInt("loanable_id"),
                                result.getInt("descriptor_id"),
                                result.getString("descriptor_name"),
                                result.getString("loanable_barcode"),
                                result.getInt("unit_number"),
                                result.getString("info_field"),
                                // For at unterstøtte shelfId == NULL:
                                result.getObject("shelf_id", Integer.class)));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load inventory for office " + officeId, e);
        }

        return new Inventory(consumables, loanables);
    }
    ;
}
