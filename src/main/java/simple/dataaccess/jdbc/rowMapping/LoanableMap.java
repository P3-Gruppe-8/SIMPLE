package simple.dataaccess.jdbc.rowMapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import simple.domain.itemmanagement.Descriptor;
import simple.domain.itemmanagement.Loanable;

public class LoanableMap {
    public static final Loanable mapRow(ResultSet result) throws SQLException {

        Descriptor descriptor = DescriptorMap.mapRow(result);

        return new Loanable(
                result.getInt("loanable_id"),
                result.getString("loanable_barcode"),
                result.getInt("unit_number"),
                result.getString("info_field"),
                result.getObject("loanable_deleted", OffsetDateTime.class),
                descriptor,
                result.getInt("shelf_id"));
    }
}
