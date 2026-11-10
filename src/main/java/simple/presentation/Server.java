package simple.presentation;

import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import io.javalin.Javalin;
import io.javalin.http.sse.SseClient;
import io.javalin.http.staticfiles.Location;
import simple.Main;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void start(int port) {
        Queue<SseClient> clients = new ConcurrentLinkedQueue<SseClient>();
        var heartbeat = Executors.newSingleThreadScheduledExecutor();
        heartbeat.scheduleAtFixedRate(() -> {
            try {
                log.info("-------------");
                for (SseClient client : clients) {
                    client.sendEvent("ping", "ping");
                    log.info("Sent ping to " + client);
                }
            } catch (Exception e) {
                log.warn("Heartbeat failed", e);
            }
        }, 15, 15, TimeUnit.SECONDS);

        Javalin.create(config -> {
            config.requestLogger
                    .http((ctx, ms) -> log.info("{} {} -> {} ({} ms)", ctx.method(), ctx.path(), ctx.statusCode(), ms));
            config.staticFiles.add("/static", Location.CLASSPATH);
            config.routes.get("/", ctx -> ctx.redirect("index.html"));
            config.routes.sse("/sse", client -> {

                client.keepAlive();
                client.onClose(() -> clients.remove(client));
                clients.add(client);

            });
            config.routes.post("/message", ctx -> {
                for (SseClient client : clients) {
                    client.sendEvent(ctx.body());
                }
            });
        }).start(port);

        log.info("Server started on port {}", port);
    }

}
