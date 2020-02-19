package com.nguyenquyhy.discordbridge;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class WebServer {
    HttpServer server = null;
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(DiscordBridge.getInstance().getConfig().webServerPort), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.createContext("/data.json", new JSONSender());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    static class JSONSender implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            DiscordBridge mod = DiscordBridge.getInstance();

            JSONObject jsonresponse = new JSONObject();

            Set<String> coll = new HashSet<>();

            mod.getGame().getServer().getOnlinePlayers().forEach(k -> coll.add(k.getName()));

            jsonresponse.put("users", coll);
            jsonresponse.put("banned", mod.TempBan.getBanned());

            JSONObject time = new JSONObject();
            time.put("day", (int)Math.floor(mod.getGame().getServer().getRunningTimeTicks() / 24000.0d));
            time.put("tick", (int)mod.getGame().getServer().getRunningTimeTicks() % 24000);
            jsonresponse.put("time", time);

            jsonresponse.put("tps", String.valueOf(mod.getGame().getServer().getTicksPerSecond()));

            String response = jsonresponse.toString();
            t.getResponseHeaders().set("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
