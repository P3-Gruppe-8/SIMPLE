package simple.dataaccess.jdbc.rowMapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import simple.domain.alert.StockAlert;
import simple.domain.alert.Urgency;
import simple.domain.itemmanagement.Descriptor;

public class StockAlertMap {
    public static StockAlert mapRow(ResultSet result) throws SQLException {

        Descriptor descriptor = DescriptorMap.mapRow(result);

        return new StockAlert(
                result.getInt("stock_alert_id"),
                Urgency.valueOf(result.getString("urgency")),
                result.getObject("raised", OffsetDateTime.class) != null,
                result.getBoolean("muted"),
                result.getInt("min_quantity"),
                descriptor);
    }
}
