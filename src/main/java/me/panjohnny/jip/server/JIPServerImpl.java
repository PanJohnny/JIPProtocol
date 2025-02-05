package me.panjohnny.jip.server;

import me.panjohnny.jip.server.router.Router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementace serveru JIP. Pro vytvoření instance využijte {@link JIPServer#create(InetSocketAddress)}.
 *
 * @author Jan Štefanča
 * @see JIPServer
 * @see ClientHandler
 * @since 1.0
 */
public final class JIPServerImpl extends JIPServer {
    public static final System.Logger LOGGER = System.getLogger(JIPServerImpl.class.getName());
    private ExecutorService threadPool;
    private ServerSocket serverSocket;
    private boolean running;
    private Thread acceptThread;
    private Router router;

    /**
     * Vytvoří novou instanci serveru JIP.
     *
     * @param address adresa serveru
     */
    public JIPServerImpl(InetSocketAddress address) {
        super(address);
        router = new Router();
    }

    /**
     * Vytvoří novou instanci serveru JIP s definovanou velikostí thread poolu.
     *
     * @param address        adresa serveru
     * @param threadPoolSize velikost thread poolu
     */
    public JIPServerImpl(InetSocketAddress address, int threadPoolSize) {
        super(address, threadPoolSize);
    }

    /**
     * Spustí server.
     *
     * @throws IOException pokud dojde k I/O erroru
     */
    @Override
    public void start() throws IOException {
        // Vytvoří thread pool
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

    /**
     * Zastaví server.
     *
     * @throws InterruptedException pokud dojde k přerušení
     * @throws IOException          pokud dojde k I/O erroru
     */
    @Override
    public void stop() throws InterruptedException, IOException {
        running = false;
        serverSocket.close();
        acceptThread.join();
        threadPool.shutdownNow();
    }

    /**
     * Přijme připojení klienta.
     */
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

    /**
     * Zjistí, zda-li je server spuštěn.
     *
     * @return true pokud je server spuštěn, jinak false
     */
    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * Získá router serveru.
     *
     * @return router serveru
     */
    @Override
    public Router getRouter() {
        return router;
    }
}