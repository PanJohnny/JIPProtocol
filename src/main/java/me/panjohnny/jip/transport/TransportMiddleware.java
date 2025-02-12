package me.panjohnny.jip.transport;

import me.panjohnny.jip.util.IOProcessor;

/**
 * Rozhraní pro middleware, který zpracovává pakety během přenosu.
 * <p>
 * Middleware může provádět operace jako šifrování, dešifrování nebo jiné transformace dat.
 * </p>
 *
 * @author Jan Štefanča
 * @see Packet
 * @see TransportLayer
 * @since 1.0
 */
public interface TransportMiddleware {
    /**
     * Zpracuje paket před jeho zápisem do výstupního proudu.
     *
     * @param packet paket k zpracování
     * @return zpracovaný paket
     */
    Packet processWrite(Packet packet);

    /**
     * Zpracuje paket po jeho přečtení ze vstupního proudu.
     *
     * @param packet paket k zpracování
     * @return zpracovaný paket
     */
    Packet processRead(Packet packet);

    /**
     * Zpracuje datový proud spojený s paketem.
     *
     * @param packet paket obsahující datový proud
     * @return IOProcessor pro zpracování datového proudu
     * @throws Exception pokud dojde k chybě při zpracování
     */
    IOProcessor processIO(Packet packet) throws Exception;
}