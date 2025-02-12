package me.panjohnny.jip.commons;

import me.panjohnny.jip.transport.Packet;

/**
 * Abstrakce pro továrnu paketu. Těmi jsou pomocníci Request a Response.
 *
 * @param <T> typ paketu
 * @author Jan Štefanča
 * @see Request
 * @see Response
 * @since 1.0
 */
public abstract class PacketFactory<T extends Packet> {
    /**
     * Vytvoří paket.
     *
     * @return paket
     */
    public abstract T fabricate();
}
