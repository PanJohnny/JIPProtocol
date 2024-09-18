package me.panjohnny.jip.client;

import me.panjohnny.jip.commons.Request;
import me.panjohnny.jip.commons.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class ClientImpl extends Client {
    private Socket socket;
    public ClientImpl(java.net.InetSocketAddress address) {
        super(address);
    }

    @Override
    public void connect() throws IOException {
        if (isConnected()) {
            throw new IllegalStateException("Already connected to the server. Use connect(InetSocketAddress) to connect to another server.");
        }
        socket = new Socket();
        socket.connect(address);
    }

    @Override
    public void connect(InetSocketAddress address) throws IOException {
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

    @Override
    public Response fetch(Request request) {
        return null;
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
