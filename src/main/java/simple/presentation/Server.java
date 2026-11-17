package simple.presentation;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.Main;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String DEV_STATIC_DIR = "src/main/resources/static";

    public static void start(int port) {

        var app = Javalin.create(config -> {
            config.requestLogger.http(
                    (ctx, ms) -> log.info("{} {} -> {} ({} ms)", ctx.method(), ctx.path(), ctx.statusCode(), ms));
            // When run from the repo, serve the source folder straight from disk for hot
            // reloads
            if (Files.isDirectory(Path.of(DEV_STATIC_DIR))) {
                log.info("Serving static files from {} (live reload on refresh)", DEV_STATIC_DIR);
                config.staticFiles.add(DEV_STATIC_DIR, Location.EXTERNAL);
            } else {
                config.staticFiles.add("/static", Location.CLASSPATH);
            }
            config.routes.get("/", ctx -> ctx.redirect("index.html"));
            config.routes.get("/ping", ctx -> ctx.result("Pong!"));

            // TODO: placeholder until shelves come from the domain
            config.routes.get(
                    "/shelves",
                    ctx -> ctx.json(List.of(
                            "a1", "a2", "a3", "b1", "b2", "b3", "c1", "c2", "c3", "d1", "d2", "d3", "e1", "e2", "e3",
                            "f1", "f2", "f3", "g1", "g2", "g3")));
            config.routes.post("/registerItemType", ctx -> {
                var itemDetails = ctx.bodyAsClass(RegisterItemTypeRequest.class);
                ctx.result("Successfully registered " + itemDetails.name());
            });
            config.routes.post("/writeOff", ctx -> {
                var itemDetails = ctx.bodyAsClass(WriteOffRequest.class);
                ctx.result("Wrote off " + itemDetails.amount() + " " + itemDetails.barcode() + "s");
            });
        });

        app.start(port);
        log.info("Server started on port {}", port);
    }
}
