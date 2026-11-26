package simple.presentation;

import static simple.presentation.RequestHeaders.requireOfficeId;
import static simple.presentation.RequestHeaders.requireStaffId;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import simple.domain.exception.AlreadyExistsException;
import simple.domain.exception.NotFoundException;
import simple.domain.exception.QuantityException;
import simple.domain.itemmanagement.Descriptor;
import simple.service.DescriptorService;

/** Handles the /descriptors routes. */
public class DescriptorController {

    private final DescriptorService descriptorService;

    public DescriptorController(DescriptorService descriptorService) {
        this.descriptorService = descriptorService;
    }

    private static NotFoundResponse noSuchItemType(String barcode) {
        return new NotFoundResponse("No item type with barcode " + barcode);
    }

    public void create(Context ctx) {
        requireStaffId(ctx); // TODO: pass to the service
        var registration = ctx.bodyAsClass(DescriptorRegistration.class);
        // the register dialog only registers consumables, and has no info field yet
        try {
            descriptorService.register(
                    registration.name(),
                    false,
                    null,
                    requireOfficeId(ctx),
                    registration.barcode(),
                    registration.shelf());
        } catch (NotFoundException e) {
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
            return;
        } catch (AlreadyExistsException e) {
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
            return;
        }
        ctx.status(HttpStatus.CREATED).result("Registered " + registration.name());
    }

    public void getOne(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        Descriptor descriptor = descriptorService
                .findByBarcode(barcode, requireOfficeId(ctx))
                .orElseThrow(() -> noSuchItemType(barcode));
        ctx.json(new DescriptorDetails(barcode, descriptor.name(), descriptor.quantity()));
    }

    public void delete(Context ctx) {
        requireStaffId(ctx);
        var barcode = ctx.pathParam("barcode");
        Descriptor deleted =
                descriptorService.deregister(barcode, requireOfficeId(ctx)).orElseThrow(() -> noSuchItemType(barcode));
        ctx.result("Deleted item type " + deleted.name());
    }

    private static int requirePositiveAmount(Context ctx) {
        var change = ctx.bodyAsClass(StockChange.class);
        if (change.amount() < 1) {
            throw new BadRequestResponse("The amount must be at least 1");
        }
        return change.amount();
    }

    public void restock(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        int amount = requirePositiveAmount(ctx);
        Descriptor descriptor = descriptorService
                .restock(barcode, requireOfficeId(ctx), amount)
                .orElseThrow(() -> noSuchItemType(barcode));
        ctx.result("Restocked " + descriptor.name());
    }

    public void writeOff(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        int amount = requirePositiveAmount(ctx);
        int officeId = requireOfficeId(ctx);
        try {
            Descriptor descriptor =
                    descriptorService.writeOff(barcode, officeId, amount).orElseThrow(() -> noSuchItemType(barcode));
            ctx.result("Wrote off " + descriptor.name());
        } catch (QuantityException e) {
            String name = barcode;
            Optional<Descriptor> descriptor = descriptorService.findByBarcode(barcode, officeId);
            if (descriptor.isPresent()) {
                name = descriptor.get().name();
            }
            ctx.status(HttpStatus.CONFLICT).result("Not enough " + name + " in stock");
        }
    }

    // TODO: replace when shelves come from the database
    private static final Map<String, List<String>> DUMMY_POSSIBLE_SHELVES = Map.of(
            "5056784902424", List.of("a1", "a2", "Kælderrum ved serverrummet, øverste hylde"),
            "5060337502290", List.of("b1"),
            "5741000142899", List.of("c1", "c2", "c3"));

    public void getPossibleShelves(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        // TODO: descriptorService.getPossibleShelves(barcode)
        descriptorService.findByBarcode(barcode, requireOfficeId(ctx)).orElseThrow(() -> noSuchItemType(barcode));
        ctx.json(DUMMY_POSSIBLE_SHELVES.getOrDefault(barcode, List.of()));
    }

    // replaces the whole possible shelves list
    public void setPossibleShelves(Context ctx) {
        requireStaffId(ctx);
        var barcode = ctx.pathParam("barcode");
        var shelves = ctx.bodyAsClass(String[].class);
        // TODO: descriptorService.setPossibleShelves(barcode, shelves), in one database
        // transaction
        Descriptor descriptor = descriptorService
                .findByBarcode(barcode, requireOfficeId(ctx))
                .orElseThrow(() -> noSuchItemType(barcode));
        ctx.result("Updated the possible shelves of " + descriptor.name());
    }

    // adds one shelf to a descriptor's possible shelves list
    public void addShelf(Context ctx) {
        var barcode = ctx.pathParam("barcode");
        var shelf = ctx.pathParam("shelf");
        // TODO: descriptorService.addShelf(barcode, shelf)
        ctx.result("Added shelf " + shelf + " to " + barcode);
    }
}
