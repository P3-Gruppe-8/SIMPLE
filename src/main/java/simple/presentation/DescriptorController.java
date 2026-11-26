package simple.presentation;

import static simple.presentation.RequestHeaders.requireOfficeId;
import static simple.presentation.RequestHeaders.requireStaffId;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import simple.domain.exception.QuantityException;
import simple.domain.itemmanagement.Descriptor;
import simple.service.DescriptorService;

/** Handles the /descriptors routes. */
public class DescriptorController {

    private final DescriptorService descriptorService;

    public DescriptorController(DescriptorService descriptorService) {
        this.descriptorService = descriptorService;
    }

    private static void notFound(Context ctx, String barcode) {
        ctx.status(HttpStatus.NOT_FOUND).result("No item type with barcode " + barcode);
    }

    public void create(Context ctx) {
        requireStaffId(ctx); // TODO: pass to the service
        var registration = ctx.bodyAsClass(DescriptorRegistration.class);
        // TODO: call descriptorService.register once shelves come from the database
        ctx.status(HttpStatus.CREATED).result("Successfully registered " + registration.name());
    }

    public void getOne(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        Optional<Descriptor> descriptor = descriptorService.findByBarcode(barcode, requireOfficeId(ctx));
        if (descriptor.isEmpty()) {
            notFound(ctx, barcode);
            return;
        }
        Descriptor found = descriptor.get();
        ctx.json(new DescriptorDetails(barcode, found.name(), found.quantity()));
    }

    public void delete(Context ctx) {
        requireStaffId(ctx);
        var barcode = ctx.pathParam("barcode");
        Optional<Descriptor> deleted = descriptorService.deregister(barcode, requireOfficeId(ctx));
        if (deleted.isEmpty()) {
            notFound(ctx, barcode);
            return;
        }
        ctx.result("Deleted item type " + deleted.get().name());
    }

    public void update(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        var patch = ctx.bodyAsClass(DescriptorPatch.class);
        if (patch.change() == 0) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Nothing to change");
            return;
        }

        Optional<Descriptor> changed;
        try {
            if (patch.change() < 0) {
                changed = descriptorService.writeOff(barcode, requireOfficeId(ctx), -patch.change());
            } else {
                changed = descriptorService.restock(barcode, requireOfficeId(ctx), patch.change());
            }
        } catch (QuantityException e) {
            String name = barcode;
            Optional<Descriptor> descriptor = descriptorService.findByBarcode(barcode, requireOfficeId(ctx));
            if (descriptor.isPresent()) {
                name = descriptor.get().name();
            }
            ctx.status(HttpStatus.CONFLICT).result("Not enough " + name + " in stock");
            return;
        }

        if (changed.isEmpty()) {
            notFound(ctx, barcode);
            return;
        }
        String verb = patch.change() < 0 ? "Wrote off " : "Restocked ";
        ctx.result(verb + changed.get().name());
    }

    // TODO: replace when shelves come from the database
    private static final Map<String, List<String>> DUMMY_POSSIBLE_SHELVES = Map.of(
            "5056784902424", List.of("a1", "a2", "Kælderrum ved serverrummet, øverste hylde"),
            "5060337502290", List.of("b1"),
            "5741000142899", List.of("c1", "c2", "c3"));

    public void getPossibleShelves(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        // TODO: descriptorService.getPossibleShelves(barcode)
        if (descriptorService.findByBarcode(barcode, requireOfficeId(ctx)).isEmpty()) {
            notFound(ctx, barcode);
        } else {
            ctx.json(DUMMY_POSSIBLE_SHELVES.getOrDefault(barcode, List.of()));
        }
    }

    // replaces the whole possible shelves list
    public void setPossibleShelves(Context ctx) {
        requireStaffId(ctx);
        var barcode = ctx.pathParam("barcode");
        var shelves = ctx.bodyAsClass(String[].class);
        // TODO: descriptorService.setPossibleShelves(barcode, shelves), in one database
        // transaction
        Optional<Descriptor> descriptor = descriptorService.findByBarcode(barcode, requireOfficeId(ctx));
        if (descriptor.isEmpty()) {
            notFound(ctx, barcode);
        } else {
            ctx.result("Updated the possible shelves of " + descriptor.get().name());
        }
    }

    // adds one shelf to a descriptor's possible shelves list
    public void addShelf(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        var shelf = ctx.pathParam("shelf");
        // TODO: descriptorService.addShelf(barcode, shelf)
        ctx.result("Added shelf " + shelf + " to " + barcode);
    }
}
