package me.panjohnny.jip.protocol.packets;

public class Packet {
    public byte[] length;
    public byte[] data;

    public Packet(byte[] length, byte[] data) {
        this.length = length;
        this.data = data;
    }

    public Packet(int length, byte[] data) {
        this.length = new byte[] {
            (byte) (length >> 24),
            (byte) (length >> 16),
            (byte) (length >> 8),
            (byte) length
        }; // max length is 2^32 - 1
        this.data = data;
    }

    public Packet() {
        length = new byte[4];
        data = new byte[0];
    }

    public void setLength(int length) {
        this.length = new byte[] {
            (byte) (length >> 24),
            (byte) (length >> 16),
            (byte) (length >> 8),
            (byte) length
        };
    }

    public int getLength() {
        return (length[0] << 24) | (length[1] << 16) | (length[2] << 8) | length[3];
    }

    public void parse(byte[] packet) {
        length = new byte[] {packet[0], packet[1], packet[2], packet[3]};
        data = new byte[packet.length - 4];
        System.arraycopy(packet, 4, data, 0, data.length);
    }

    public void useData(byte[] data) {
        this.data = data;
        setLength(data.length);
    }

    public byte[] serialize() {
        // length[4] + data[length]
        byte[] packet = new byte[4 + data.length];
        System.arraycopy(length, 0, packet, 0, 4);
        System.arraycopy(data, 0, packet, 4, data.length);
        return packet;
    }

    public String toString() {
        return new String(data);
    }

    public byte[] getData() {
        return data;
    }
}
