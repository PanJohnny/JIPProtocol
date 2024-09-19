package me.panjohnny.jip;

import me.panjohnny.jip.client.Client;
import me.panjohnny.jip.security.ClientSecurityLayer;
import me.panjohnny.jip.security.ServerSecurityLayer;
import me.panjohnny.jip.server.JIPServer;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws Exception {
        var server = JIPServer.create(new InetSocketAddress(8080));
        server.start();
        System.out.println("Server started");
        var client = Client.create(new InetSocketAddress(8080));
        System.out.println("Client created");
        client.connect();
        System.out.println("Client connected");
    }

    public static void handshakeTest() throws Exception {
        ClientSecurityLayer clientSecurityLayer = new ClientSecurityLayer();

        var handshake = clientSecurityLayer.createHandshakePacket();

        ServerSecurityLayer serverSecurityLayer = new ServerSecurityLayer();

        serverSecurityLayer.acceptClientHandshake(handshake.serialize());

        var serverHandshake = serverSecurityLayer.createHandshakePacket();

        clientSecurityLayer.acceptServerHandshake(serverHandshake.serialize());

        /*
         * Secure message to server
         */
        var helloServer = "Hello, Server!".getBytes();
        var encrypted = clientSecurityLayer.encrypt(helloServer);
        var decrypted = serverSecurityLayer.decrypt(encrypted);
        System.out.println(new String(decrypted));
        assert Arrays.equals(encrypted, decrypted);

        /**
         * Secure message to client
         */
        var helloClient = "Hello, Client!".getBytes();
        encrypted = serverSecurityLayer.encrypt(helloClient);
        decrypted = clientSecurityLayer.decrypt(encrypted);
        System.out.println(new String(decrypted));
        assert Arrays.equals(encrypted, decrypted);
    }
}
