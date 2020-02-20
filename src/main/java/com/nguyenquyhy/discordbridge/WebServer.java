package com.nguyenquyhy.discordbridge;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        server.createContext("/", new HTTPSender("text/html", Paths.get(
                DiscordBridge.getInstance().getConfigDir().toAbsolutePath().toString() + "index.html"
        )));
        server.createContext("/favicon.png", new FileSender("image/png", Paths.get(
                DiscordBridge.getInstance().getConfigDir().toAbsolutePath().toString() + "favicon.png"
        )));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public void stop() {
        server.stop(0);
    }


    static String read(Path filePath) throws IOException {
        StringBuilder  stringBuilder;
        FileReader fileReader     = null;
        BufferedReader bufferedReader = null;
        try {
            stringBuilder  = new StringBuilder();
            fileReader     = new FileReader(filePath.toString());
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } finally {
            if (bufferedReader != null) try { bufferedReader.close(); } catch (Exception ex) { /* Do Nothing */ }
            if (fileReader     != null) try { fileReader    .close(); } catch (Exception ex) { /* Do Nothing */ }
        }

        return stringBuilder.toString();
    }

    class HTTPSender implements HttpHandler {
        Path p;
        String m;
        public HTTPSender(String _m, Path _p) { m = _m; p = _p; }

        @Override
        public void handle(HttpExchange t) throws IOException {
            DiscordBridge.getInstance().getLogger().info("Web Request : " + p.toString());
            String response = read(p);
            t.getResponseHeaders().set("Content-Type", m + "; charset=" + StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    class FileSender implements HttpHandler {
        Path p;
        String m;
        public FileSender(String _m, Path _p) { m = _m; p = _p; }

        @Override
        public void handle(HttpExchange t) throws IOException {
            DiscordBridge.getInstance().getLogger().info("Web Request : " + p.toString());
            byte[] response = new byte[65536];
            InputStream is = new FileInputStream(p.toString());
            int numOfBytes = is.read(response);

            t.getResponseHeaders().set("Content-Type", m + ";");
            t.sendResponseHeaders(200, numOfBytes);
            OutputStream os = t.getResponseBody();
            os.write(response, 0, numOfBytes - 1);
            os.close();
        }
    }

    class JSONSender implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            DiscordBridge mod = DiscordBridge.getInstance();

            mod.getLogger().info("JSON Request");

            JSONObject jsonresponse = new JSONObject();

            Set<String> coll = new HashSet<>();
            mod.getGame().getServer().getOnlinePlayers().forEach(k -> coll.add(k.getName()));
            jsonresponse.put("users", coll);

            jsonresponse.put("banned", mod.TempBan.getBanned());

            jsonresponse.put("tick", mod.serverState.getTick());

            JSONObject time = new JSONObject();
            time.put("day", mod.serverState.getDay());
            time.put("hour", mod.serverState.getHour());
            time.put("min", mod.serverState.getMin());
            jsonresponse.put("time", time);

            jsonresponse.put("tps", mod.serverState.getTPS());

            String response = jsonresponse.toString();
            t.getResponseHeaders().set("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
