package simple.dataaccess.jdbc.rowMapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import simple.domain.employee.ITStaff;

public class ITStaffMap {
    public static final ITStaff mapRow(ResultSet result) throws SQLException {
        return new ITStaff(
                result.getInt("it_staff_id"),
                result.getString("username"),
                result.getString("first_name"),
                result.getString("last_name"),
                result.getInt("office_id"));
    }
}
