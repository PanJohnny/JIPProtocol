package me.panjohnny.jip.server;

import me.panjohnny.jip.server.router.Router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class JIPServerImpl extends JIPServer {
    private ExecutorService threadPool;
    private ServerSocket serverSocket;
    private boolean running;
    private Thread acceptThread;
    public JIPServerImpl(InetSocketAddress address) {
        super(address);
    }

    public JIPServerImpl(InetSocketAddress address, int threadPoolSize) {
        super(address, threadPoolSize);
    }

    @Override
    public void start() throws IOException {
        // Create threadPool
        threadPool = Executors.newFixedThreadPool(threadPoolSize);
        serverSocket = new ServerSocket();
        serverSocket.bind(address);
        acceptThread = new Thread(() -> {
            while (running) {
                accept();
            }
        });
        running = true;
        acceptThread.start();
    }

    @Override
    public void stop() throws InterruptedException, IOException {
        running = false;
        acceptThread.join();
        threadPool.shutdownNow();
        serverSocket.close();
    }

    private void accept() {
        try {
            var socket = serverSocket.accept();
            threadPool.submit(() -> {
                try {
                    new ClientHandler(socket);
                } catch (IOException e) {
                    System.Logger logger = System.getLogger(JIPServerImpl.class.getName());
                    logger.log(System.Logger.Level.ERROR, "Failed to create a client handler", e);
                }
            });
        } catch (IOException e) {
            System.Logger logger = System.getLogger(JIPServerImpl.class.getName());
            logger.log(System.Logger.Level.ERROR, "Failed to accept a client", e);
        }
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
