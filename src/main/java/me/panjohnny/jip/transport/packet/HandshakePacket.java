package me.panjohnny.jip.transport.packet;

import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.util.Bytes;

/**
 * Handshake paket
 *
 * <p>Paket používaný k navázání zabezpečeného spojení mezi klientem a serverem.</p>
 *
 * @author Jan Štefanča
 * @see Packet
 * @since 1.0
 */
public class HandshakePacket extends Packet {
    public HandshakePacket(byte[] publicKey) {
        super();
        this.data = new Bytes(publicKey);
    }

    @Override
    public void prepare() {
        length = new byte[0]; // Handshake paket nemá délku
    }

    @Override
    public int getLength() {
        return -1;
    }
}