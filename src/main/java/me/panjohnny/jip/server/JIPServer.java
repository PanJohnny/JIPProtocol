package me.panjohnny.jip.server;

import me.panjohnny.jip.server.router.Router;

import java.io.IOException;
import java.net.InetSocketAddress;

public abstract sealed class JIPServer permits JIPServerImpl {
    protected final InetSocketAddress address;
    protected final int threadPoolSize;
    public JIPServer(InetSocketAddress address) {
        this.address = address;
        this.threadPoolSize = 10;
    }

    public JIPServer(InetSocketAddress address, int threadPoolSize) {
        this.address = address;
        this.threadPoolSize = threadPoolSize;
    }

    public abstract void start() throws IOException;
    public abstract void stop() throws InterruptedException;

    public abstract boolean isRunning();

    public abstract Router getRouter();

    public InetSocketAddress getAddress() {
        return address;
    }

    public static JIPServer create(InetSocketAddress address) {
        return new JIPServerImpl(address);
    }

    public static JIPServer create(InetSocketAddress address, int threadPoolSize) {
        return new JIPServerImpl(address, threadPoolSize);
    }
}
