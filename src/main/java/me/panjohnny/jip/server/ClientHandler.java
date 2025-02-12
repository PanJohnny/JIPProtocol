package me.panjohnny.jip.server;

import me.panjohnny.jip.commons.JIPVersion;
import me.panjohnny.jip.commons.Response;
import me.panjohnny.jip.commons.StatusCode;
import me.panjohnny.jip.security.ServerSecurityLayer;
import me.panjohnny.jip.server.router.DynamicRoute;
import me.panjohnny.jip.server.router.Route;
import me.panjohnny.jip.server.router.Router;
import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.transport.TransportLayer;
import me.panjohnny.jip.transport.packet.RequestPacket;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Zpracovává požadavky klienta.
 *
 * @author Jan Štefanča
 * @since 1.0
 */
public class ClientHandler {
    private final Socket socket;
    private final ServerSecurityLayer securityLayer;
    private final TransportLayer transportLayer;
    private final Router router;
    private final static System.Logger LOGGER = System.getLogger(ClientHandler.class.getName());

    public static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(10);

    /**
     * Vytvoří nového zpracovatele klienta.
     *
     * @param socket socket pro komunikaci s klientem
     * @param router router pro směrování požadavků
     * @throws IOException pokud se nepodaří inicializovat transportní vrstvu
     */
    public ClientHandler(Socket socket, Router router) throws IOException {
        this.socket = socket;
        this.transportLayer = new TransportLayer(socket.getInputStream(), socket.getOutputStream());
        this.securityLayer = new ServerSecurityLayer();
        this.router = router;
    }

    /**
     * Zpracuje požadavky klienta.
     */
    public void handle() {
        handshake();
        handleRequests();
    }

    /**
     * Provede handshake s klientem.
     */
    private void handshake() {
        try {
            var clientHandshake = transportLayer.readN(294);
            securityLayer.acceptClientHandshake(clientHandshake);
            var handshakePacket = securityLayer.createHandshakePacket();
            transportLayer.writePacket(handshakePacket);
            transportLayer.useMiddleware(securityLayer);
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Nepodařilo se provést handshake s klientem, uzavírám spojení...", e);
            try {
                socket.close();
            } catch (IOException ex) {
                LOGGER.log(System.Logger.Level.ERROR, "Nepodařilo se uzavřít socket", ex);
            }
        }
    }

    private boolean ready;

    /**
     * Zpracovává požadavky klienta.
     */
    private void handleRequests() {
        long lastTime = System.nanoTime();
        ready = false;
        // Server je připraven
        try {
            transportLayer.writePacket(new Packet(1, new byte[]{1}));
            ready = true;
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Nepodařilo se oznámit připravenost serveru, uzavírám spojení...", e);
        }

        while (ready && !socket.isClosed() && System.currentTimeMillis() - lastTime < TIMEOUT_NANOS) {
            handleRequest();
            lastTime = System.nanoTime();
        }

        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.log(System.Logger.Level.ERROR, "Nepodařilo se uzavřít socket", e);
            }
        }
    }

    /**
     * Zpracuje jeden požadavek klienta.
     */
    private void handleRequest() {
        try {
            Packet packet = transportLayer.readPacket();
            // Spojení bylo uzavřeno klientem
            if (packet == null) {
                ready = false;
                return;
            }

            RequestPacket requestPacket = RequestPacket.parse(packet);

            Route route = router.getRoute(requestPacket.getResource());
            if (route != null) {
                var version = JIPVersion.getDefault().toString();
                var status = StatusCode.OK.toString();
                Response response = new Response(version, status);
                var handler = router.getHandler(requestPacket.getResource());
                if (route instanceof DynamicRoute dn) {
                    var params = dn.parseParameters(requestPacket.getResource());
                    handler.handle(requestPacket, response, params);
                } else {
                    handler.handle(requestPacket, response);
                }
                transportLayer.writePacket(response.fabricate());
            } else {
                var version = JIPVersion.getDefault().toString();
                var status = StatusCode.NOT_FOUND.toString();
                Response response = new Response(version, status);
                transportLayer.writePacket(response.fabricate());
            }
            //transportLayer.flush();
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Nepodařilo se přečíst paket od klienta, uzavírám spojení...", e);
            try {
                socket.close();
            } catch (IOException ex) {
                LOGGER.log(System.Logger.Level.ERROR, "Nepodařilo se uzavřít socket", ex);
            }
        }
    }
}