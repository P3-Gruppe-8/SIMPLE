package simple.presentation;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

/** Reads the headers api.js sends with every request. */
public class RequestHeaders {

    public static String requireStaffId(Context ctx) {
        var staffId = ctx.header("X-Staff-Id");
        if (staffId == null || staffId.isBlank()) {
            throw new UnauthorizedResponse("Scan your staff ID first");
        }
        return staffId;
    }

    // the tablet's office, chosen in the debug menu
    public static int requireOfficeId(Context ctx) {
        var officeId = ctx.header("X-Office-Id");
        if (officeId == null || officeId.isBlank()) {
            throw new BadRequestResponse("Choose an office in the debug menu first");
        }
        try {
            return Integer.parseInt(officeId);
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("Invalid office ID: " + officeId);
        }
    }
}
