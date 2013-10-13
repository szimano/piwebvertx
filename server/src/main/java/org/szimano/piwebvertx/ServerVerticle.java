package org.szimano.piwebvertx;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
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
