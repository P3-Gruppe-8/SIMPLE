package simple;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        int port = 7070;

        Javalin.create(config -> {
            config.requestLogger
                    .http((ctx, ms) -> log.info("{} {} -> {} ({} ms)", ctx.method(), ctx.path(), ctx.statusCode(), ms));
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        }).start(port);

        log.info("Server started on port {}", port);
    }
}
