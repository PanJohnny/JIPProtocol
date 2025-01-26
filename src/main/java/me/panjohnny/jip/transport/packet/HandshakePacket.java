package me.panjohnny.jip.transport.packet;

import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.util.Bytes;

/**
 * Handshake packet
 *
 * <p>Packet used to establish a secure connection between the client and the server.</p>
 * @author Jan Štefanča
 * @see Packet
 */
public class HandshakePacket extends Packet {
    public HandshakePacket(byte[] publicKey) {
        super();
        this.data = new Bytes(publicKey);
    }

    @Override
    public void prepare() {
        length = new byte[0]; // Handshake packet has no length
    }

    @Override
    public int getLength() {
        return -1;
    }
}