package simple.presentation;

import static simple.presentation.RequestHeaders.requireOfficeId;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import simple.service.ITStaffService;

/** Handles the /staff routes. */
public class ITStaffController {
    private final ITStaffService itStaffService;

    public ITStaffController(ITStaffService itStaffService) {
        this.itStaffService = itStaffService;
    }

    public void checkExists(Context context) {
        String username = context.pathParam("username");
        if (itStaffService.existsByUsername(username, requireOfficeId(context))) {
            context.result(username);
        } else {
            context.status(HttpStatus.NOT_FOUND).result("Unknown staff ID " + username);
        }
    }
}
