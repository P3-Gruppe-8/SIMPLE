package simple.presentation;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

/**
 * Handles the /descriptors routes. TODO: placeholders until the service layer
 * exists.
 */
public class DescriptorController {

    public void create(Context ctx) {
        var registration = ctx.bodyAsClass(DescriptorRegistration.class);
        ctx.status(HttpStatus.CREATED).result("Successfully registered " + registration.name());
    }

    public void getOne(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        ctx.result("Details for " + barcode + " aren't available yet");
    }

    public void delete(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        ctx.result("Deleted item type " + barcode);
    }

    public void update(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        var patch = ctx.bodyAsClass(DescriptorPatch.class);
        if (patch.change() < 0) {
            ctx.result("Wrote off " + -patch.change() + " " + barcode + "s");
        } else if (patch.change() > 0) {
            ctx.result("Restocked " + patch.change() + " " + barcode + "s");
        } else {
            ctx.status(HttpStatus.BAD_REQUEST).result("Nothing to change");
        }
    }

    public void addShelf(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        var shelf = ctx.pathParam("shelf");
        ctx.result("Added shelf " + shelf + " to " + barcode);
    }

    public void removeShelf(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        var shelf = ctx.pathParam("shelf");
        ctx.result("Removed shelf " + shelf + " from " + barcode);
    }
}
