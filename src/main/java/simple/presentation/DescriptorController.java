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

    public void create(Context context) {
        requireStaffId(context); // TODO: pass to the service
        var registration = context.bodyAsClass(DescriptorRegistration.class);
        // the register dialog only registers consumables, and has no info field yet
        try {
            descriptorService.register(
                    registration.name(),
                    false,
                    null,
                    requireOfficeId(context),
                    registration.barcode(),
                    registration.shelf());
        } catch (NotFoundException e) {
            context.status(HttpStatus.NOT_FOUND).result(e.getMessage());
            return;
        } catch (AlreadyExistsException e) {
            context.status(HttpStatus.CONFLICT).result(e.getMessage());
            return;
        }
        context.status(HttpStatus.CREATED).result("Registered " + registration.name());
    }

    public void getOne(Context context) {
        var barcode = context.pathParam("barcode");
        Descriptor descriptor = descriptorService
                .findByBarcode(barcode, requireOfficeId(context))
                .orElseThrow(() -> noSuchItemType(barcode));
        context.json(new DescriptorDetails(barcode, descriptor.name(), descriptor.quantity()));
    }

    public void delete(Context context) {
        requireStaffId(context);
        var barcode = context.pathParam("barcode");
        Descriptor deleted = descriptorService
                .deregister(barcode, requireOfficeId(context))
                .orElseThrow(() -> noSuchItemType(barcode));
        context.result("Deleted item type " + deleted.name());
    }

    private static int requirePositiveAmount(Context context) {
        var change = context.bodyAsClass(StockChange.class);
        if (change.amount() < 1) {
            throw new BadRequestResponse("The amount must be at least 1");
        }
        return change.amount();
    }

    public void restock(Context context) {
        var barcode = context.pathParam("barcode");
        int amount = requirePositiveAmount(context);
        Descriptor descriptor = descriptorService
                .restock(barcode, requireOfficeId(context), amount)
                .orElseThrow(() -> noSuchItemType(barcode));
        context.result("Restocked " + descriptor.name());
    }

    public void writeOff(Context context) {
        var barcode = context.pathParam("barcode");
        int amount = requirePositiveAmount(context);
        int officeId = requireOfficeId(context);
        try {
            Descriptor descriptor =
                    descriptorService.writeOff(barcode, officeId, amount).orElseThrow(() -> noSuchItemType(barcode));
            context.result("Wrote off " + descriptor.name());
        } catch (QuantityException e) {
            String name = barcode;
            Optional<Descriptor> descriptor = descriptorService.findByBarcode(barcode, officeId);
            if (descriptor.isPresent()) {
                name = descriptor.get().name();
            }
            context.status(HttpStatus.CONFLICT).result("Not enough " + name + " in stock");
        }
    }

    // TODO: replace when shelves come from the database
    private static final Map<String, List<String>> DUMMY_POSSIBLE_SHELVES = Map.of(
            "5056784902424", List.of("a1", "a2", "Kælderrum ved serverrummet, øverste hylde"),
            "5060337502290", List.of("b1"),
            "5741000142899", List.of("c1", "c2", "c3"));

    public void getPossibleShelves(Context context) {
        var barcode = context.pathParam("barcode");
        // TODO: descriptorService.getPossibleShelves(barcode)
        descriptorService.findByBarcode(barcode, requireOfficeId(context)).orElseThrow(() -> noSuchItemType(barcode));
        context.json(DUMMY_POSSIBLE_SHELVES.getOrDefault(barcode, List.of()));
    }

    // replaces the whole possible shelves list
    public void setPossibleShelves(Context context) {
        requireStaffId(context);
        var barcode = context.pathParam("barcode");
        var shelves = context.bodyAsClass(String[].class);
        // TODO: descriptorService.setPossibleShelves(barcode, shelves), in one database
        // transaction
        Descriptor descriptor = descriptorService
                .findByBarcode(barcode, requireOfficeId(context))
                .orElseThrow(() -> noSuchItemType(barcode));
        context.result("Updated the possible shelves of " + descriptor.name());
    }

    // adds one shelf to a descriptor's possible shelves list
    public void addShelf(Context context) {
        var barcode = context.pathParam("barcode");
        var shelf = context.pathParam("shelf");
        // TODO: descriptorService.addShelf(barcode, shelf)
        context.result("Added shelf " + shelf + " to " + barcode);
    }
}
