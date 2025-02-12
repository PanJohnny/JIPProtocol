package me.panjohnny.jip.server;

import me.panjohnny.jip.server.router.Router;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Server JIP slouží k obsluze požadavků klientů. Pro získání instance využijte {@link #create(InetSocketAddress)}.
 *
 * @author Jan Štefanča
 * @see #start()
 * @see #stop()
 * @see #getRouter()
 * @see JIPServerImpl
 * @since 1.0
 */
public abstract sealed class JIPServer permits JIPServerImpl {
    protected final InetSocketAddress address;
    protected final int threadPoolSize;

    /**
     * Chráněný konstruktor serveru JIP.
     *
     * @param address adresa serveru
     */
    protected JIPServer(InetSocketAddress address) {
        this.address = address;
        this.threadPoolSize = 10;
    }

    /**
     * Chráněný konstruktor serveru JIP s definovanou velikostí thread poolu.
     *
     * @param address        adresa serveru
     * @param threadPoolSize velikost thread poolu
     */
    protected JIPServer(InetSocketAddress address, int threadPoolSize) {
        this.address = address;
        this.threadPoolSize = threadPoolSize;
    }

    /**
     * Vytvoří nový server JIP.
     *
     * @param address adresa serveru
     * @return nová instance serveru JIP
     */
    public static JIPServer create(InetSocketAddress address) {
        return new JIPServerImpl(address);
    }

    /**
     * Vytvoří nový server JIP s definovanou velikostí thread poolu.
     *
     * @param address        adresa serveru
     * @param threadPoolSize velikost thread poolu
     * @return nová instance serveru JIP
     */
    public static JIPServer create(InetSocketAddress address, int threadPoolSize) {
        return new JIPServerImpl(address, threadPoolSize);
    }

    /**
     * Spustí server.
     *
     * @throws IOException pokud dojde k I/O erroru
     */
    public abstract void start() throws IOException;

    /**
     * Zastaví server.
     *
     * @throws InterruptedException pokud dojde k přerušení
     * @throws IOException          pokud dojde k I/O erroru
     */
    public abstract void stop() throws InterruptedException, IOException;

    /**
     * Zjistí, zda-li je server spuštěn.
     *
     * @return true pokud je server spuštěn, jinak false
     */
    public abstract boolean isRunning();

    /**
     * Získá router serveru.
     *
     * @return router serveru
     */
    public abstract Router getRouter();

    /**
     * Získá adresu serveru.
     *
     * @return adresa serveru
     */
    public InetSocketAddress getAddress() {
        return address;
    }
}