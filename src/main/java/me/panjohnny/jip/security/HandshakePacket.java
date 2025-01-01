package me.panjohnny.jip.security;

import me.panjohnny.jip.transport.Packet;

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
        this.data = publicKey;
    }

    @Override
    public byte[] serialize() {
        return data;
    }
}