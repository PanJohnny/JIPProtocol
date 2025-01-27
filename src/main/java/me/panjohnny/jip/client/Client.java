package me.panjohnny.jip.client;


import me.panjohnny.jip.commons.JIPVersion;
import me.panjohnny.jip.commons.Request;
import me.panjohnny.jip.transport.packet.ResponsePacket;
import me.panjohnny.jip.security.SecureTransportException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Client that connects to the server and sends requests
 * @author Jan Štefanča
 * @see ClientImpl
 */
public abstract sealed class Client permits ClientImpl {
    protected InetSocketAddress address;

    /**
     * Creates a new client with the given address
     * @param address address of the server
     */
    public Client(InetSocketAddress address) {
        this.address = address;
    }

    /**
     * Connects to the server
     * @throws IOException if an I/O error occurs
     * @throws SecureTransportException if the secure transport layer fails
     */
    public abstract void connect() throws Exception;

    /**
     * Connects to the server with the given address. If the client is already connected, it reconnects to the new address
     * @param address address of the server
     * @throws SecureTransportException if the secure transport layer fails
     * @throws IOException if an I/O error occurs
     */
    public abstract void connect(InetSocketAddress address) throws Exception;

    /**
     * Fetches response from the server
     * @param req request to send to the server
     * @return response from the server
     * @throws SecureTransportException if the secure transport layer fails
     * @throws IOException if an I/O error occurs
     */
    public abstract ResponsePacket fetch(Request req) throws Exception;

    /**
     * Fetches response from the server
     * @param resource path to the resource
     * @return response from the server
     * @throws SecureTransportException if the secure transport layer fails
     * @throws IOException if an I/O error occurs
     */
    public ResponsePacket fetch(String resource) throws Exception {
        return fetch(new Request(JIPVersion.getDefault().toString(), resource));
    }
    /**
     * Closes the connection to the server
     * @throws IOException if an I/O error occurs
     */
    public abstract void close() throws IOException;

    /**
     * Checks if the client socket is closed
     * @return true if the client socket is closed, false otherwise
     */
    public abstract boolean isClosed();

    /**
     * Gets the address of the server
     * @return address of the server
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    /**
     * Creates a new client with the given address
     * @param address address of the server
     * @return new client
     */
    public static Client create(InetSocketAddress address) {
        return new ClientImpl(address);
    }


    /**
     * Set a consumer to further configure the socket when connecting to the server
     * @param socketConsumer a consumer to configure the connection
     * @return instance of client, same as the object, created in order to be used after consumer
     */
    public abstract Client useSocketConfigurator(Consumer<Socket> socketConsumer);
}
