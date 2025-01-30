package me.panjohnny.jip.server;

import me.panjohnny.jip.server.router.Router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class JIPServerImpl extends JIPServer {
    public static final System.Logger LOGGER = System.getLogger(JIPServerImpl.class.getName());
    private ExecutorService threadPool;
    private ServerSocket serverSocket;
    private boolean running;
    private Thread acceptThread;
    private Router router;
    public JIPServerImpl(InetSocketAddress address) {
        super(address);
        router = new Router();
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
            try {
                while (running) {
                    accept();
                }
            } catch (Exception e) {
                if (!running) {
                    return;
                }
                LOGGER.log(System.Logger.Level.WARNING, "Nepovedlo se přijmout klienta", e);
            }
        });
        running = true;
        acceptThread.start();
    }

    @Override
    public void stop() throws InterruptedException, IOException {
        running = false;
        serverSocket.close();
        acceptThread.join();
        threadPool.shutdownNow();
    }

    private void accept() {
        try {
            var socket = serverSocket.accept();
            socket.setSoTimeout(30000); // po 30 sekundách bude klient odpojen
            threadPool.submit(() -> {
                try {
                    new ClientHandler(socket, router).handle();
                } catch (IOException e) {
                    if (!running) {
                        return;
                    }
                    LOGGER.log(System.Logger.Level.WARNING, "Nepodařilo se vytvořit ", e);
                }
            });
        } catch (IOException e) {
            if (!running) {
                return;
            }
            LOGGER.log(System.Logger.Level.WARNING, "Nepodařilo se přijmout připojení klienta", e);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Router getRouter() {
        return router;
    }
}
