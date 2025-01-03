package me.panjohnny.jip.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import me.panjohnny.jip.commons.RequestPacket;
import me.panjohnny.jip.commons.ResponsePacket;
import me.panjohnny.jip.security.ServerSecurityLayer;
import me.panjohnny.jip.server.router.Router;
import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.transport.TransportLayer;

/**
 * Handles client requests.
 *
 * @author Jan Štefanča
 */
public class ClientHandler {
    private final Socket socket;
    private final ServerSecurityLayer securityLayer;
    private final TransportLayer transportLayer;
    private final Router router;
    private final static System.Logger LOGGER = System.getLogger(ClientHandler.class.getName());

    public static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(10);

    public ClientHandler(Socket socket, Router router) throws IOException {
        this.socket = socket;
        this.transportLayer = new TransportLayer(socket.getInputStream(), socket.getOutputStream());
        this.securityLayer = new ServerSecurityLayer();
        this.router = router;
    }

    public void handle() {
        handshake();
        handleRequests();
    }

    private void handshake() {
        try {
            var clientHandshake = transportLayer.readN(294);
            securityLayer.acceptClientHandshake(clientHandshake);
            var handshakePacket = securityLayer.createHandshakePacket();
            transportLayer.writePacket(handshakePacket);
            transportLayer.useMiddleware(securityLayer);
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to handshake with the client, closing...", e);
            try {
                socket.close();
            } catch (IOException ex) {
                LOGGER.log(System.Logger.Level.ERROR, "Failed to close the socket", ex);
            }
        }
    }

    private boolean ready;
    private void handleRequests() {
        long lastTime = System.nanoTime();
        ready = false;
        // Server ready
        try {
            transportLayer.writePacket(new Packet(1, new byte[] {1}));
            ready = true;
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to notify ready state, closing...", e);
        }

        while (ready && !socket.isClosed() && System.currentTimeMillis() - lastTime < TIMEOUT_NANOS) {
            handleRequest();
            lastTime = System.nanoTime();
        }

        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.log(System.Logger.Level.ERROR, "Failed to close socket", e);
            }
        }
    }

    private void handleRequest() {
        try {
            Packet packet = transportLayer.readPacket();
            // Connection was closed by the client
            if (packet == null) {
                ready = false;
                return;
            }
            RequestPacket requestPacket = RequestPacket.parse(packet);

            if (router.hasRoute(requestPacket.getResource())) {
                ResponsePacket responsePacket = ResponsePacket.OK;
                router.getHandler(requestPacket.getResource()).handle(requestPacket, responsePacket);
                transportLayer.writePacket(responsePacket);
            } else {
                ResponsePacket responsePacket = new ResponsePacket("JIP/1.0", "Not Found");
                transportLayer.writePacket(responsePacket);
            }
            //transportLayer.flush();
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to read packet from the client, closing...", e);
            try {
                socket.close();
            } catch (IOException ex) {
                LOGGER.log(System.Logger.Level.ERROR, "Failed to close the socket", ex);
            }
        }   
    }
}
