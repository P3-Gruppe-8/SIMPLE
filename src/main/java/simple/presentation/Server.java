package simple.presentation;

import static io.javalin.apibuilder.ApiBuilder.*;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.Main;
import simple.domain.exception.DataAccessException;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String DEV_STATIC_DIR = "src/main/resources/static";

    public static void start(
            int port,
            DescriptorController descriptorController,
            ITStaffController itStaffController,
            OfficeController officeController,
            ShelfController shelfController) {

        Javalin app = Javalin.create(config -> {
            config.requestLogger.http((context, ms) ->
                    log.info("{} {} -> {} ({} ms)", context.method(), context.path(), context.statusCode(), ms));
            // When run from the repo, serve the source folder straight from disk for hot
            // reloads
            if (Files.isDirectory(Path.of(DEV_STATIC_DIR))) {
                log.info("Serving static files from {} (live reload on refresh)", DEV_STATIC_DIR);
                try {
                    String staticDir = Path.of(DEV_STATIC_DIR).toRealPath().toString();
                    config.staticFiles.add(staticDir, Location.EXTERNAL);
                } catch (java.io.IOException e) {
                    throw new java.io.UncheckedIOException("Could not resolve the static files directory", e);
                }
            } else {
                config.staticFiles.add("/static", Location.CLASSPATH);
            }
            config.routes.get("/", context -> context.redirect("index.html"));
            config.routes.get("/ping", context -> context.result("Pong!"));

            config.routes.exception(DataAccessException.class, (e, context) -> {
                log.error("Database error on {} {}", context.method(), context.path(), e);
                context.status(HttpStatus.SERVICE_UNAVAILABLE).result("Database error: " + e.getMessage());
            });

            config.routes.apiBuilder(() -> {
                path("/descriptors", () -> {
                    post(descriptorController::create);
                    path("/{barcode}", () -> {
                        get(descriptorController::getOne);
                        post("/restock", descriptorController::restock);
                        post("/write-off", descriptorController::writeOff);
                        delete(descriptorController::delete);
                        path("/shelves", () -> {
                            get(descriptorController::getPossibleShelves);
                            put(descriptorController::setPossibleShelves);
                            path("/{shelf}", () -> {
                                put(descriptorController::addShelf);
                            });
                        });
                    });
                });
                path("/shelves", () -> {
                    get(shelfController::getAll);
                });
                path("/staff/{username}", () -> {
                    get(itStaffController::checkExists);
                });
                path("/offices", () -> {
                    get(officeController::getAll);
                });
            });
        });

        app.start(port);
        log.info("Server started on port {}", port);
    }
}
