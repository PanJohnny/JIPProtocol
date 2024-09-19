package me.panjohnny.jip.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TransportLayer {
    private InputStream input;
    private OutputStream output;
    public TransportLayer(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    public void waitForData() throws IOException {
        while (input.available() == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void writePacket(Packet packet) throws IOException {
        output.write(packet.serialize());
    }

    public Packet readPacket() throws IOException {
        byte[] header = readN(4);
        int len = (header[0] << 24) | (header[1] << 16) | (header[2] << 8) | header[3];
        byte[] data = readN(len);
        return new Packet(len, data);
    }

    public byte[] readAll() throws IOException {
        return input.readAllBytes();
    }

    public byte[] readN(int len) throws IOException {
        return input.readNBytes(len);
    }
}
