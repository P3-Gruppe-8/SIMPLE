package simple.presentation;

import java.util.List;

import io.javalin.http.Context;
import simple.domain.alert.StockAlert;
import simple.domain.alert.Urgency;
import simple.domain.itemmanagement.Descriptor;
import simple.service.AlertService;

public class AlertController {

    public final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // TODO: remove when service layer is finished
    private static final List<StockAlert> DUMMY_ALERTS = List.of(
            new StockAlert(1, Urgency.INFO, true, false, 10,
                    new Descriptor(1, "Printer_Cartridge", false, 15, "info_fields", "info_templates", 1)),
            new StockAlert(2, Urgency.WARNING, true, false, 10,
                    new Descriptor(2, "Pencil", false, 10, "info_fields", "info_templates", 1)),
            new StockAlert(3, Urgency.CRITICAL, true, false, 10,
                    new Descriptor(3, "3D_Fillament", false, 0, "info_fields", "info_templates", 1)));

    // TODO: change when service layer is finished
    public void getDummyAlerts(Context context) {
        System.out.println(DUMMY_ALERTS);
        context.json(DUMMY_ALERTS);
    }
}
