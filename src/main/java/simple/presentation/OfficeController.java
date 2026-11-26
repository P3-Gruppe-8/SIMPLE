package simple.presentation;

import io.javalin.http.Context;
import simple.service.OfficeService;

/** Handles the /offices routes. */
public class OfficeController {
    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    public void getAll(Context context) {
        context.json(officeService.getAll());
    }
}
