package me.panjohnny.jip.transport;

import me.panjohnny.jip.util.ByteUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

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
        output.write(packet.length);
        for (byte[] b : packet.getData().bytes()) {
            output.write(b);
        }
        packet.free();
    }

    /**
     * Reads a packet from the input stream
     * @return packet or null if the stream closed
     * @throws IOException if an I/O error occurs
     */
    public Packet readPacket() throws IOException {
        byte[] header = readN(4);
        if (header.length != 4) {
            return null;
        }
        int len = ByteUtil.byteArray4ToInt(header);
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
