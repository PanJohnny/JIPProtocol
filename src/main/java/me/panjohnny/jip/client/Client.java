package me.panjohnny.jip.client;


import me.panjohnny.jip.commons.Request;
import me.panjohnny.jip.commons.Response;
import me.panjohnny.jip.security.SecureTransportException;

import java.io.IOException;
import java.net.InetSocketAddress;

public abstract sealed class Client permits ClientImpl {
    protected InetSocketAddress address;

    public Client(InetSocketAddress address) {
        this.address = address;
    }

    public abstract void connect() throws IOException, SecureTransportException;

    public abstract void connect(InetSocketAddress address) throws IOException, SecureTransportException;

    public abstract boolean isConnected();

    public abstract Response fetch(Request request);

    public abstract void close() throws IOException;

    public abstract boolean isClosed();
    public InetSocketAddress getAddress() {
        return address;
    }

    public static Client create(InetSocketAddress address) {
        return new ClientImpl(address);
    }
}
