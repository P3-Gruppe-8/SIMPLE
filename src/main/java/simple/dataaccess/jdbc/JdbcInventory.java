package simple.dataaccess.jdbc;

import java.sql.*;
import java.util.ArrayList;
import simple.dataaccess.config.Databaseconnection;
import simple.dataaccess.jdbc.rowMapping.DescriptorMap;
import simple.dataaccess.jdbc.rowMapping.LoanableMap;
import simple.domain.exception.DataAccessException;
import simple.domain.itemmanagement.Descriptor;
import simple.domain.itemmanagement.Inventory;
import simple.domain.itemmanagement.Loanable;
import simple.domain.repository.InventoryRepository;

public class JdbcInventory implements InventoryRepository {

    @Override
    public Inventory findOverviewByOffice(int officeId) {

        ArrayList<Descriptor> consumables = new ArrayList<Descriptor>();
        ArrayList<Loanable> loanables = new ArrayList<Loanable>();

        // Left join makes a cartesian product between
        String inventorySql = """
                SELECT descriptors.*,
                       loanables.loanable_id,
                       loanables.loanable_barcode,
                       loanables.unit_number,
                       loanables.info_field AS loanable_info_field,
                       loanables.loanable_deleted,
                       loanables.shelf_id
                FROM descriptors
                LEFT JOIN loanables
                  ON loanables.descriptor_id = descriptors.descriptor_id
                 AND descriptors.is_loanable = true
                 AND loanables.loanable_deleted IS NULL
                WHERE descriptors.office_id = ?
                  AND descriptors.descriptor_deleted IS NULL
                ORDER BY descriptors.descriptor_name, descriptors.descriptor_id, loanables.unit_number
                """;

        Databaseconnection databaseConnection = new Databaseconnection();

        try (Connection connection = databaseConnection.getDbConnection();
                PreparedStatement statement = connection.prepareStatement(inventorySql)) {
            statement.setInt(1, officeId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    if (!result.getBoolean("is_loanable")) {
                        consumables.add(DescriptorMap.mapRow(result));
                    } else if (result.getObject("loanable_id", Integer.class) != null) {
                        loanables.add(LoanableMap.mapRow(result));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: Failed to load inventory for office " + officeId, e);
        }

        return new Inventory(consumables, loanables);
    }
    ;
}
