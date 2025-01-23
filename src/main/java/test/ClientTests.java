package test;

import me.panjohnny.jip.client.Client;
import me.panjohnny.jip.security.SecureTransportException;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ClientTests {
    public static final System.Logger LOGGER = System.getLogger(ClientTests.class.getName());
    public static void main(String[] args) throws Exception {
        // Test #1, handshake
        //handshakeTest();
        // Test #2, 4 GB
        theoreticalMax();
        LOGGER.log(System.Logger.Level.INFO, "All tests executed disposed");
    }

    public static void handshakeTest() throws IOException, SecureTransportException {
        for (int i = 0; i < 30; i++) {
            var client = Client.create(new InetSocketAddress(8080));
            var current = System.nanoTime();
            client.connect();
            var elapsed = System.nanoTime() - current;
            LOGGER.log(System.Logger.Level.INFO, "Connection time {0} ms", elapsed / 1_000_000);
            // Send some data to the server

            //RequestTimer.watch(() -> client.fetch("/hello"), "Static hello");
            //RequestTimer.watch(() -> client.fetch("/hello/John"), "Dynamic hello");
            //RequestTimer.watch(() -> client.fetch("/large"), "Large file");


            client.disconnect();
        }
    }

    public static void theoreticalMax() throws IOException, SecureTransportException {
        var client = Client.create(new InetSocketAddress(8080));
        var current = System.nanoTime();
        client.connect();
        var elapsed = System.nanoTime() - current;
        LOGGER.log(System.Logger.Level.INFO, "Connection time {0} ms", elapsed / 1_000_000);
        RequestTimer.watch(() -> client.fetch("/large"), "Large file");
        client.disconnect();
    }
}
