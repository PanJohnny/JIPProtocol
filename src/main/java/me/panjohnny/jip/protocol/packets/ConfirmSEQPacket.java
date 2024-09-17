package me.panjohnny.jip.protocol.packets;

public class ConfirmSEQPacket extends Packet {
    public ConfirmSEQPacket(byte[] confirmSEQ) {
        super();
        useData(confirmSEQ);
    }

    @Override
    public byte[] serialize() {
        return data;
    }
}
