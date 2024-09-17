package me.panjohnny.jip.protocol.packets;

public class HandshakePacket extends Packet {
    public HandshakePacket(byte[] confirmSEQ, byte[] publicKey) {
        super();
        if (confirmSEQ.length != 2) {
            throw new IllegalArgumentException("Invalid confirmSEQ length");
        }
        byte[] data = new byte[2 + publicKey.length];
        System.arraycopy(confirmSEQ, 0, data, 0, 2);
        System.arraycopy(publicKey, 0, data, 2, publicKey.length);
        useData(data);
    }

    @Override
    public byte[] serialize() {
        return data;
    }
}