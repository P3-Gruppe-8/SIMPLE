package simple.dataaccess.jdbc.rowMapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import simple.domain.itemmanagement.Descriptor;

public class descriptorMap {
    public static final Descriptor mapRow(ResultSet result) throws SQLException {
        return new Descriptor(
                result.getInt("descriptor_id"),
                result.getString("descriptor_name"),
                result.getBoolean("is_loanable"),
                result.getInt("quantity"),
                result.getString("info_field"),
                result.getString("info_template"),
                result.getInt("office_id"));
    }
}
