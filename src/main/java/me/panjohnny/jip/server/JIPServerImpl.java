package me.panjohnny.jip.server;

import me.panjohnny.jip.server.router.Router;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class JIPServerImpl extends JIPServer {
    public JIPServerImpl(InetSocketAddress address) {
        super(address);
    }

    public JIPServerImpl(InetSocketAddress address, int threadPoolSize) {
        super(address, threadPoolSize);
    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public void stop() throws InterruptedException {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Router getRouter() {
        return null;
    }
}
