package me.panjohnny.jip.client;


import me.panjohnny.jip.commons.JIPVersion;
import me.panjohnny.jip.commons.Request;
import me.panjohnny.jip.security.SecureTransportException;
import me.panjohnny.jip.transport.packet.ResponsePacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Klient slouží k připojení klienta k serveru. Pro získání instance využijte {@link #create(InetSocketAddress)}.
 *
 * @author Jan Štefanča
 * @see #connect()
 * @see #useSocketConfigurator(Consumer)
 * @see ClientImpl
 * @since 1.0
 */
public abstract sealed class Client permits ClientImpl {
    protected InetSocketAddress address;

    /**
     * Chráněný konstruktor klienta
     *
     * @param address adresa serveru
     */
    protected Client(InetSocketAddress address) {
        this.address = address;
    }

    /**
     * Připojí klienta na předem definovanou adresu. Pro připojení na jinou adresu, či přepojení využijte {@link #connect(InetSocketAddress)}.
     *
     * @throws SecureTransportException pokud selže bezpečnostní vrstva
     * @throws IOException              pokud dojde k I/O erroru
     * @see #connect(InetSocketAddress)
     * @see #close()
     */
    public abstract void connect() throws Exception;

    /**
     * Připojí či přepojí klienta na definovanou adresu. Pro připojení na dříve použitou adresu využijte {@link #connect()}.
     *
     * @param address adresa serveru
     * @throws SecureTransportException pokud selže bezpečnostní vrstva
     * @throws IOException              pokud dojde k I/O erroru
     */
    public abstract void connect(InetSocketAddress address) throws Exception;

    /**
     * Zašle žádost na server a pokusí se získat odpověď.
     *
     * @param req žádost
     * @return odpověď ze serveru
     * @throws SecureTransportException pokud selže bezpečnostní vrstva
     * @throws IOException              pokud dojde k I/O erroru
     * @see Request
     * @see ResponsePacket
     */
    public abstract ResponsePacket fetch(Request req) throws Exception;

    /**
     * Zašle žádost na server s aktuální verzí knihovny a definovanou cestou a pokusí se získat odpověď.
     *
     * @param resource cesta
     * @return odpověď ze serveru
     * @throws SecureTransportException pokud selže bezpečnostní vrstva
     * @throws IOException              pokud dojde k I/O erroru
     * @see Request
     * @see ResponsePacket
     */
    public ResponsePacket fetch(String resource) throws Exception {
        return fetch(new Request(JIPVersion.getDefault().toString(), resource));
    }

    /**
     * Ukončí spojení se serverem uzavřením soketu. (Klient je tedy odpojen.)
     *
     * @throws IOException pokud dojde k I/O erroru
     */
    public abstract void close() throws IOException;

    /**
     * Zjistí, zda-li je soket uzavřen.
     *
     * @return true pokud je soket uzavřen, jinak false
     * @see #close()
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean isClosed();

    /**
     * Získá poslední využitou adresu serveru. Pokud nebylo využito metody {@link #connect(InetSocketAddress)}, je touto adresou adresa, která byla využita na tvorbu instance.
     *
     * @return poslední využitá adresa serveru
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    /**
     * Vytvoří nového klienta.
     *
     * @param address adresa serveru
     * @return nová instance klienta
     */
    public static Client create(InetSocketAddress address) {
        return new ClientImpl(address);
    }


    /**
     * Nastaví consumer pro další konfiguraci soketu při připojování k serveru.
     *
     * <p>Ukázka využití:</p>
     * <pre>{@code
     * var client = Client.create(addr).useSocketConfigurator((s) -> {
     * s.setSoTimeout(20000); // maximální doba blokování read
     * })}</pre>
     *
     * @param socketConsumer consumer pro konfiguraci připojení
     * @return aktuální instance klienta
     */
    public abstract Client useSocketConfigurator(Consumer<Socket> socketConsumer);
}
