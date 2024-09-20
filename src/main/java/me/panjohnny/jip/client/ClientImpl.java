package me.panjohnny.jip.client;

import me.panjohnny.jip.commons.Request;
import me.panjohnny.jip.commons.Response;
import me.panjohnny.jip.security.ClientSecurityLayer;
import me.panjohnny.jip.security.SecureTransportException;
import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.transport.TransportLayer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class ClientImpl extends Client {
    private Socket socket;
    private ClientSecurityLayer securityLayer;
    private TransportLayer transportLayer;

    private final System.Logger logger = System.getLogger(ClientImpl.class.getName());

    public ClientImpl(java.net.InetSocketAddress address) {
        super(address);
    }

    @Override
    public void connect() throws IOException, SecureTransportException {
        if (isConnected()) {
            throw new IllegalStateException("Already connected to the server. Use connect(InetSocketAddress) to connect to another server.");
        }
        socket = new Socket();
        socket.connect(address);
        transportLayer = new TransportLayer(socket.getInputStream(), socket.getOutputStream());
        securityLayer = new ClientSecurityLayer();

        handshake();
    }

    @Override
    public void connect(InetSocketAddress address) throws SecureTransportException, IOException {
        if (isConnected()) {
            // Connect to another server
            this.address = address;
            socket.connect(address);
        } else {
            this.address = address;
            connect();
        }
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    private void handshake() {
        try {
            var handshake = securityLayer.createHandshakePacket();
            transportLayer.writePacket(handshake);
            var serverHandshake = transportLayer.readN(256);
            securityLayer.acceptServerHandshake(serverHandshake);
            transportLayer.useMiddleware(securityLayer);
            logger.log(System.Logger.Level.INFO, "Secure connection with server established");
        } catch (IOException | SecureTransportException e) {
            logger.log(System.Logger.Level.ERROR, "Failed to handshake with the server, closing...", e);
            try {
                close();
            } catch (IOException e1) {
                logger.log(System.Logger.Level.ERROR, "Failed to close transport layer: " + e1.getMessage(), e1);
                throw new RuntimeException(e1);
            }
        }
    }

    @Override
    public Response fetch(Request request) {
        try {
            // Server ready
            Packet serverReady = transportLayer.readPacket();
            if (serverReady.getLength() != 1 || serverReady.getData()[0] != 1) {
                logger.log(System.Logger.Level.ERROR, "Server is not ready to receive the request - Invalid server ready packet: {0}", serverReady);
                return null;
            }
            // Server ready
            logger.log(System.Logger.Level.INFO, "Server is ready to receive the request");
            transportLayer.writePacket(request);
            return Response.parse(transportLayer.readPacket());
        } catch (IOException e) {
            logger.log(System.Logger.Level.ERROR, "Failed to send the request to the server", e);
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }
}
