package simple.presentation;

import io.javalin.http.Context;
import java.util.List;

/**
 * Handles the /shelves routes. TODO: placeholder until shelves come from the
 * domain.
 */
public class ShelfController {

    public void getAll(Context ctx) {
        ctx.json(List.of(
                "a1",
                "a2",
                "a3",
                "Lager A3",
                "b1",
                "Kælderrum ved serverrummet, øverste hylde",
                "b2",
                "c1",
                "c2",
                "Reolsystem-Nordfløj-Mellemgang-Sektion12",
                "c3",
                "IT-kontor skab 2",
                "g2"));
    }
}
