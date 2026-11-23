package simple.presentation;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import java.util.Map;

/**
 * Handles the /descriptors routes. TODO: placeholders until the service layer
 * exists.
 */
public class DescriptorController {

    private static String requireStaffId(Context ctx) {
        var staffId = ctx.header("X-Staff-Id");
        if (staffId == null || staffId.isBlank()) {
            throw new UnauthorizedResponse("Scan your staff ID first");
        }
        return staffId;
    }

    public void create(Context ctx) {
        requireStaffId(ctx); // TODO: pass to the service
        var registration = ctx.bodyAsClass(DescriptorRegistration.class);
        ctx.status(HttpStatus.CREATED).result("Successfully registered " + registration.name());
    }

    // TODO: replace when database is connected
    private static final Map<String, DescriptorDetails> DUMMY_DESCRIPTORS = Map.of(
            "5056784902424", new DescriptorDetails("5056784902424", "Monster Rio Punch", 24),
            "5060337502290", new DescriptorDetails("5060337502290", "Monster Ultra White", 12),
            "5741000142899", new DescriptorDetails("5741000142899", "Booster 0 kcal", 15),
            "5061013940870", new DescriptorDetails("5061013940870", "Monster Original Zero Sugar", 1000));

    public void getOne(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        var descriptor = DUMMY_DESCRIPTORS.get(barcode);
        if (descriptor == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("No item type with barcode " + barcode);
        } else {
            ctx.json(descriptor);
        }
    }

    public void delete(Context ctx) {
        requireStaffId(ctx);
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
        requireStaffId(ctx);
        var barcode = ctx.pathParam("barcode");
        var shelf = ctx.pathParam("shelf");
        ctx.result("Added shelf " + shelf + " to " + barcode);
    }

    public void removeShelf(Context ctx) {
        requireStaffId(ctx);
        var barcode = ctx.pathParam("barcode");
        var shelf = ctx.pathParam("shelf");
        ctx.result("Removed shelf " + shelf + " from " + barcode);
    }
}
