package me.panjohnny.jip.transport;

import me.panjohnny.jip.util.AESUtil;
import me.panjohnny.jip.util.ByteUtil;
import me.panjohnny.jip.util.IOProcessor;

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

    public void writePacket(Packet packet) throws Exception {
        packet.prepare();
        IOProcessor proc = null;
        if (middleware != null) {
            if (packet.hasConnectedStream()) {
                proc = middleware.processIO(packet);
                packet.streamLen += AESUtil.IV_LENGTH;
                packet.updateLen();
            } else {
                packet = middleware.processWrite(packet);
            }
        }

        output.write(packet.length);

        if (proc != null) {
            proc.init(output);
            proc.process(packet.getConnectedStream(), output);
            proc.close(packet.getConnectedStream());
        } else {
            for (byte[] b : packet.getData().bytes()) {
                output.write(b);
            }
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
