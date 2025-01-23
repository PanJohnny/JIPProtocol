package test;

import me.panjohnny.jip.server.JIPServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class ServerProvider {
    public static void main(String[] args) throws IOException {
        System.Logger logger = System.getLogger(ServerProvider.class.getName());
        // Vytvoří server na portu 8080
        var server = JIPServer.create(new InetSocketAddress(8080));

        // Přidá handler pro žádost na /hello, tato routa je statická, nevyžaduje tedy parametry (_)
        server.getRouter().route("/hello", (request, response, _) -> {
            response.sendString("Hello, World!");
        });

        // Přidá dynamickou routu na /hello/[name], tato routa vyžaduje parametr name
        server.getRouter().route("/hello/[name]", (req, res, params) -> {
            var name = params.get("name");
            res.sendString("Hello, " + name + "!"); // Odpoví klientovi Hello, [name]!
        });
        byte[] data = new byte[(int) (0.5*Math.pow(10, 9))-15]; // 4 GB
        Arrays.fill(data, (byte) 'A');
        // Testování maximálního přenosu dat
        server.getRouter().route("/large", (req, res, _) -> {
            res.setBody(data);
        });

        server.start();

        // Resources disposed
        logger.log(System.Logger.Level.INFO, "Server started");
    }
}
