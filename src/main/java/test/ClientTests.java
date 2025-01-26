package test;

import me.panjohnny.jip.client.Client;
import me.panjohnny.jip.security.SecureTransportException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ClientTests {
    public static final System.Logger LOGGER = System.getLogger(ClientTests.class.getName());
    public static void main(String[] args) throws Exception {
        // Test #1, handshake
        handshakeTest();
        // Test #2, 4 GB
        //theoreticalMax();
        LOGGER.log(System.Logger.Level.INFO, "All tests executed disposed");
    }

    public static void handshakeTest() throws Exception {
        var client = Client.create(new InetSocketAddress(8080));
        var current = System.nanoTime();
        client.connect();
        System.out.println("Client connected in " + (System.nanoTime() - current) / 1_000_000 + " ms");
        for (int i = 0; i < 30; i++) {
            // Využije mé utility
            var a = RequestTimer.watch(() -> client.fetch("/max"), "max");
            a = null;
            System.gc();
        }
        client.disconnect();
    }

    public static void theoreticalMax() throws Exception {
        var client = Client.create(new InetSocketAddress(8080));
        var current = System.nanoTime();
        client.connect();
        var elapsed = System.nanoTime() - current;
        LOGGER.log(System.Logger.Level.INFO, "Connection time {0} ms", elapsed / 1_000_000);
        var res = RequestTimer.watch(() -> client.fetch("/large"), "Large file");
        client.disconnect();
        File large2 = new File("large2.txt");
        large2.createNewFile();
        FileOutputStream fos = new FileOutputStream(large2);
        fos.write(res.getBody());
        fos.flush();
    }
}
