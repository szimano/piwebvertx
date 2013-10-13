package org.szimano.piwebvertx;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class ServerVerticle extends Verticle {

    public void start() {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                String file = "";
                if (req.path().equals("/")) {
                    file = "index.html";
                } else if (!req.path().contains("..")) {
                    file = req.path();
                }
                req.response().sendFile("web/" + file);
            }
        });

        JsonObject config = new JsonObject().putString("prefix", "/comm");

        JsonArray permitted = new JsonArray();
        permitted.add(new JsonObject());

        //watch out, this means ALLOW-ALL messages via JS. not production friendly!
        vertx.createSockJSServer(server).bridge(config, permitted, permitted);

        server.listen(9999, "localhost");

        vertx.eventBus().registerHandler("buttonbus", new Handler<Message>() {
            @Override
            public void handle(Message message) {
                System.out.println("Button state is: "+message.body());
            }
        });

        System.out.println("Server started");
    }
}
