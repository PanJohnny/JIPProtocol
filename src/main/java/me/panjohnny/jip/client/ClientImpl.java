package me.panjohnny.jip.client;

import me.panjohnny.jip.commons.RequestPacket;
import me.panjohnny.jip.commons.ResponsePacket;
import me.panjohnny.jip.security.ClientSecurityLayer;
import me.panjohnny.jip.security.SecureTransportException;
import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.transport.TransportLayer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * An implementation of JIP Client. By default, there is 10 second SO timeout. Before connecting to the server call to {@link Client#connect()} is required. After sending all request to the server use {@link Client#disconnect()}
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
    public void connect() throws IOException, SecureTransportException {
        if (isConnected()) {
            throw new IllegalStateException("Already connected to the server. Use connect(InetSocketAddress) to connect to another server.");
        }
        socket = new Socket();
        socket.setSoTimeout(10000); // 10 sec timeout by default
        if (socketConfigurator != null)
            socketConfigurator.accept(socket);
        socket.connect(address);
        transportLayer = new TransportLayer(socket.getInputStream(), socket.getOutputStream());
        securityLayer = new ClientSecurityLayer();

        handshake();

        Packet serverReady = transportLayer.readPacket();
        if (serverReady.getLength() != 1 || serverReady.getData()[0] != 1) {
            LOGGER.log(System.Logger.Level.ERROR, "Server is not ready to receive the request - Invalid server ready packet: {0}", serverReady);
            close();
        }
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
        } catch (IOException | SecureTransportException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to handshake with the server, closing...", e);
            try {
                close();
            } catch (IOException e1) {
                LOGGER.log(System.Logger.Level.ERROR, "Failed to close transport layer: " + e1.getMessage(), e1);
                throw new RuntimeException(e1);
            }
        }
    }

    @Override
    public ResponsePacket fetch(RequestPacket requestPacket) throws SecureTransportException, IOException {
        //connect()
        // Server is ready now
        transportLayer.writePacket(requestPacket);

        //disconnect();
        return ResponsePacket.parse(transportLayer.readPacket());
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
    public void disconnect() throws IOException {
        close();
        socket = null;
    }

    @Override
    public Client useSocketConfigurator(Consumer<Socket> socketConsumer) {
        this.socketConfigurator = socketConsumer;
        return this;
    }
}
