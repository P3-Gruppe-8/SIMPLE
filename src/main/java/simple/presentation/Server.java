package simple.presentation;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.Main;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void start(int port) {

        var app = Javalin.create(config -> {
            config.requestLogger.http(
                    (ctx, ms) -> log.info("{} {} -> {} ({} ms)", ctx.method(), ctx.path(), ctx.statusCode(), ms));
            config.staticFiles.add("/static", Location.CLASSPATH);
            config.routes.get("/", ctx -> ctx.redirect("index.html"));
            config.routes.get("/ping", ctx -> ctx.result("Pong!"));
            config.routes.post("/registerItemType", ctx -> {
                var itemDetails = ctx.bodyAsClass(RegisterItemTypeRequest.class);
                ctx.result("Successfully registered " + itemDetails.name());
            });
        });

        app.start(port);
        log.info("Server started on port {}", port);

        // jdbctest hej = new jdbctest();
    }
}
