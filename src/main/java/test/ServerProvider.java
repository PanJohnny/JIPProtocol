package test;

import me.panjohnny.jip.server.JIPServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class ServerProvider {
    public static void main(String[] args) throws IOException {
        System.Logger logger = System.getLogger(ServerProvider.class.getName());
        // Vytvoří server na portu 8080
        var server = JIPServer.create(new InetSocketAddress(8080));

        // Pole bajtů
        // Přidá cestu /max, ta je statická, nevyžaduje tedy parametry (_)
        server.getRouter().route("/max", (request, response, _) -> {
            try {
                response.sendFile(new File("large.txt"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        server.start();

        // Resources disposed
        logger.log(System.Logger.Level.INFO, "Server started");
    }

    public static void maiasdn(String[] args) {


        // Přidá dynamickou routu na /hello/[name], tato routa vyžaduje parametr name
//        server.getRouter().route("/hello/[name]", (req, res, params) -> {
//            var name = params.get("name");
//            res.sendString("Hello, " + name + "!"); // Odpoví klientovi Hello, [name]!
//        });
//
//        File file = new File("large.txt");
//        // Testování maximálního přenosu dat
//        server.getRouter().route("/large", (req, res, _) -> {
//            try {
//                res.sendFile(file);
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        });
    }
}
