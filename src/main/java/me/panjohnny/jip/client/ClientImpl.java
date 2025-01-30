package me.panjohnny.jip.client;

import me.panjohnny.jip.commons.Request;
import me.panjohnny.jip.security.ClientSecurityLayer;
import me.panjohnny.jip.security.SecureTransportException;
import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.transport.TransportLayer;
import me.panjohnny.jip.transport.packet.ResponsePacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * An implementation of JIP Client. By default, there is 10 second SO timeout. Before connecting to the server call to {@link Client#connect()} is required. After sending all request to the server use {@link Client#close()}
 *
 * @see Client
 * @see Socket#setSoTimeout(int) 
 * @see Client#useSocketConfigurator(Consumer)
 * @author Jan Štefanča
 */
public final class ClientImpl extends Client {
    private Socket socket;
    private ClientSecurityLayer securityLayer;
    private TransportLayer transportLayer;
    private Consumer<Socket> socketConfigurator;

    private static final System.Logger LOGGER = System.getLogger(ClientImpl.class.getName());

    public ClientImpl(java.net.InetSocketAddress address) {
        super(address);
    }

    @Override
    public void connect() throws Exception {
        if (!isClosed()) {
            throw new IllegalStateException("Klient je připojen k serveru. Použijte #connect(InetSocketAddress) k přepojení na jinou adresu, nebo #close k uzavření spojení.");
        }
        socket = new Socket();
        socket.setSoTimeout(60000); // timeout one minute
        if (socketConfigurator != null)
            socketConfigurator.accept(socket);
        socket.connect(address);
        transportLayer = new TransportLayer(socket.getInputStream(), socket.getOutputStream());
        securityLayer = new ClientSecurityLayer();

        handshake();

        Packet serverReady = transportLayer.readPacket();
        if (serverReady == null || serverReady.getLength() != 1 || serverReady.getData().at(0)[0] != 1) {
            close();
            throw new SecureTransportException("Nepodařilo se navázat zabezpečného spojení. Server vrátil špatnou odpověď.");
        }
    }

    @Override
    public void connect(InetSocketAddress address) throws Exception {
        if (!isClosed()) {
            this.address = address;
            socket.connect(address);
        } else {
            this.address = address;
            connect();
        }
    }

    private void handshake() throws SecureTransportException {
        try {
            var handshake = securityLayer.createHandshakePacket();
            transportLayer.writePacket(handshake);
            var serverHandshake = transportLayer.readN(256);
            securityLayer.acceptServerHandshake(serverHandshake);
            transportLayer.useMiddleware(securityLayer);
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Nepodařilo se dokončit proces handshake, uzavírám soket...", e);
            try {
                close();
            } catch (IOException e1) {
                LOGGER.log(System.Logger.Level.ERROR, "Nepodařilo se uzavřít transportní vrstvu: " + e1.getMessage(), e1);
                throw new RuntimeException(e1);
            }
            throw new SecureTransportException("Nepodařilo se dokončit proces handshake", e);
        }
    }

    @Override
    public ResponsePacket fetch(Request req) throws Exception {
        //connect()
        // Server is ready now
        transportLayer.writePacket(req.fabricate());

        var packet = transportLayer.readPacket();
        if (packet == null) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to read response packet from the server");
            return null;
        }
        //disconnect();
        return ResponsePacket.parse(packet);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean isClosed() {
        return socket != null && socket.isClosed();
    }

    @Override
    public Client useSocketConfigurator(Consumer<Socket> socketConsumer) {
        this.socketConfigurator = socketConsumer;
        return this;
    }
}
