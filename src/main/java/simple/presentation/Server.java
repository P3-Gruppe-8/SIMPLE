package simple.presentation;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import io.javalin.Javalin;
import simple.Main;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void start(int port) {

        Javalin.create(config -> {
            config.requestLogger
                    .http((ctx, ms) -> log.info("{} {} -> {} ({} ms)", ctx.method(), ctx.path(), ctx.statusCode(), ms));
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        }).start(port);

        log.info("Server started on port {}", port);
    }

}
