package simple.presentation;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.Main;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void start(int port) {
        Javalin app = Javalin.create(config -> {
            config.requestLogger.http(
                    (ctx, ms) -> log.info("{} {} -> {} ({} ms)", ctx.method(), ctx.path(), ctx.statusCode(), ms));
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        });

        app.start(port);
        log.info("Server started on port {}", port);
    }
}
