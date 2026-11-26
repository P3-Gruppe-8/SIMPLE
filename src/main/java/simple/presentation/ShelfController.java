package simple.presentation;

import static simple.presentation.RequestHeaders.requireOfficeId;

import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.List;
import simple.domain.itemmanagement.Shelf;
import simple.service.ShelfService;

/** Handles the /shelves routes. */
public class ShelfController {
    private final ShelfService shelfService;

    public ShelfController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    public void getAll(Context ctx) {
        List<String> names = new ArrayList<>();
        for (Shelf shelf : shelfService.getAllByOffice(requireOfficeId(ctx))) {
            names.add(shelf.name());
        }
        ctx.json(names);
    }
}
