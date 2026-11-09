package simple;

import io.javalin.Javalin;

/**
 * Hello world!
 *
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        var app = Javalin.create(config -> {
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        }).start(7070);
    }
}
