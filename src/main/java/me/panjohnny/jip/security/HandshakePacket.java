package me.panjohnny.jip.security;

import me.panjohnny.jip.transport.Packet;

/**
 * Handshake packet
 *
 * <p>Packet used to establish a secure connection between the client and the server.</p>
 *
 * <p>TThe first two bytes is confirmSEQ - confirm sequence and the other bytes are the public key.</p>
 */
public class HandshakePacket extends Packet {
    public HandshakePacket(byte[] confirmSEQ, byte[] publicKey) {
        super();
        byte[] data = new byte[2 + publicKey.length];
        System.arraycopy(confirmSEQ, 0, data, 0, 2);
        System.arraycopy(publicKey, 0, data, 2, publicKey.length);
        this.data = data;
    }

    @Override
    public byte[] serialize() {
        return data;
    }
}