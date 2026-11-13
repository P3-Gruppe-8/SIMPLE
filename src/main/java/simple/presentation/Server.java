package simple.presentation;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.Main;
import simple.dataaccess.jdbc.jdbctest;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void start(int port) {

        var app = Javalin.create(config -> {
            config.requestLogger.http(
                    (ctx, ms) -> log.info("{} {} -> {} ({} ms)", ctx.method(), ctx.path(), ctx.statusCode(), ms));
            config.staticFiles.add("/static", Location.CLASSPATH);
            config.routes.get("/", ctx -> ctx.redirect("index.html"));
        });

        app.start(port);
        log.info("Server started on port {}", port);

        jdbctest hej = new jdbctest();
    }
}
