package me.panjohnny.jip;

import me.panjohnny.jip.client.Client;
import me.panjohnny.jip.commons.RequestPacket;
import me.panjohnny.jip.server.JIPServer;

import java.net.InetSocketAddress;

public class Test {
    public static void main(String[] args) throws Exception {
        var server = JIPServer.create(new InetSocketAddress(8080));
        // Configure router before accepting requests
        server.getRouter().route("/test", ((request, response) -> {

        }));
        server.start();
        var client = Client.create(new InetSocketAddress(8080));
        client.connect();
        // Send some data to the server
        var notFound = client.fetch(new RequestPacket("JIP/1.0", "/"));
        System.Logger logger = System.getLogger(Test.class.getName());
        logger.log(System.Logger.Level.INFO, "404:\n{0}\n\n", notFound);

        var test = client.fetch(new RequestPacket("JIP/1.0", "/test"));
        logger.log(System.Logger.Level.INFO, "test:\n{0}\n\n", test);

        client.disconnect();
        server.stop();

        // Resources disposed
        logger.log(System.Logger.Level.INFO, "All resources disposed");
    }
}
