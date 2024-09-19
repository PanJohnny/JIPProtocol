package me.panjohnny.jip.server;

import java.io.IOException;
import java.net.Socket;

import me.panjohnny.jip.security.ServerSecurityLayer;
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
    }

    private void handshake() {
        try {
            var clientHandshake = transportLayer.readN(294);
            securityLayer.acceptClientHandshake(clientHandshake);
            var handshakePacket = securityLayer.createHandshakePacket();
            transportLayer.writePacket(handshakePacket);
        } catch (Exception e) {
            System.Logger logger = System.getLogger(ClientHandler.class.getName());
            logger.log(System.Logger.Level.ERROR, "Failed to handshake with the client, closing...", e);
            try {
                socket.close();
            } catch (IOException ex) {
                logger.log(System.Logger.Level.ERROR, "Failed to close the socket", ex);
            }
        }
    }
}
