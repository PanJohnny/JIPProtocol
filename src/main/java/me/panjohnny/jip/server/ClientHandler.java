package me.panjohnny.jip.server;

import java.io.IOException;
import java.net.Socket;

import me.panjohnny.jip.commons.Request;
import me.panjohnny.jip.commons.Response;
import me.panjohnny.jip.security.ServerSecurityLayer;
import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.transport.TransportLayer;

public class ClientHandler {
    private Socket socket;
    private ServerSecurityLayer securityLayer;
    private TransportLayer transportLayer;
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.transportLayer = new TransportLayer(socket.getInputStream(), socket.getOutputStream());
        this.securityLayer = new ServerSecurityLayer();
        handshake();
        handleRequest();
    }

    private void handshake() {
        System.Logger logger = System.getLogger(ClientHandler.class.getName());
        try {
            var clientHandshake = transportLayer.readN(294);
            securityLayer.acceptClientHandshake(clientHandshake);
            var handshakePacket = securityLayer.createHandshakePacket();
            transportLayer.writePacket(handshakePacket);
            transportLayer.useMiddleware(securityLayer);
            logger.log(System.Logger.Level.INFO, "Secure connection with client established");
        } catch (Exception e) {
            logger.log(System.Logger.Level.ERROR, "Failed to handshake with the client, closing...", e);
            try {
                socket.close();
            } catch (IOException ex) {
                logger.log(System.Logger.Level.ERROR, "Failed to close the socket", ex);
            }
        }
    }

    public void handleRequest() {
        // TODO implement properly
        System.Logger logger = System.getLogger(ClientHandler.class.getName());
        try {
            Packet packet = transportLayer.readPacket();
            System.out.println("Received packet: " + packet);
            Request request = Request.parse(packet);
            // log the request
            logger.log(System.Logger.Level.INFO, "Received request: " + request);
            Response response = new Response("JIP/1.0", "OK");
            transportLayer.writePacket(response);
        } catch (IOException e) {
            
            logger.log(System.Logger.Level.ERROR, "Failed to read packet from the client, closing...", e);
            try {
                socket.close();
            } catch (IOException ex) {
                logger.log(System.Logger.Level.ERROR, "Failed to close the socket", ex);
            }
        }   
    }
}
