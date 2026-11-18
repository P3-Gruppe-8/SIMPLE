package simple.presentation;

import io.javalin.http.Context;
import java.util.List;

/** Handles the /shelves routes. TODO: placeholder until shelves come from the domain. */
public class ShelfController {

    public void getAll(Context ctx) {
        ctx.json(List.of(
                "a1", "a2", "a3", "b1", "b2", "b3", "c1", "c2", "c3", "d1", "d2", "d3", "e1", "e2", "e3", "f1", "f2",
                "f3", "g1", "g2", "g3"));
    }
}
