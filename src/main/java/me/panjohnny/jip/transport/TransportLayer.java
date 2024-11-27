package me.panjohnny.jip.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TransportLayer {
    private final InputStream input;
    private final OutputStream output;
    private TransportMiddleware middleware;
    public TransportLayer(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    public void useMiddleware(TransportMiddleware middleware) {
        this.middleware = middleware;
    }

    public void writePacket(Packet packet) throws IOException {
        packet.prepare();
        if (middleware != null) {
            packet = middleware.processWrite(packet);
        }
        output.write(packet.serialize());
    }

    public Packet readPacket() throws IOException {
        byte[] header = readN(4);
        int len = (header[0] << 24) | (header[1] << 16) | (header[2] << 8) | header[3];
        byte[] data = readN(len);
        Packet packet = new Packet(len, data);
        if (middleware != null) {
            packet = middleware.processRead(packet);
        }
        return packet;
    }

    public byte[] readAll() throws IOException {
        return input.readAllBytes();
    }

    public byte[] readN(int len) throws IOException {
        return input.readNBytes(len);
    }

    public void flush() throws IOException {
        output.flush();
    }
}
