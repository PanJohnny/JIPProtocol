package me.panjohnny.jip.transport;

import me.panjohnny.jip.util.AESUtil;
import me.panjohnny.jip.util.ByteUtil;
import me.panjohnny.jip.util.IOProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Transportní vrstva slouží pro přenos dat skrz datové proudy.
 * <p>
 * Třída poskytuje metody pro zápis a čtení paketů, které mohou být zpracovány middlewarem.
 * </p>
 *
 * @author Jan Štefanča
 * @see Packet
 * @see TransportMiddleware
 * @since 1.0
 */
public class TransportLayer {
    private final InputStream input;
    private final OutputStream output;
    private TransportMiddleware middleware;

    /**
     * Vytvoří novou transportní vrstvu se specifikovanými vstupním a výstupním proudem.
     *
     * @param input  vstupní proud
     * @param output výstupní proud
     */
    public TransportLayer(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Nastaví middleware pro zpracování paketů.
     *
     * @param middleware middleware pro zpracování paketů
     */
    public void useMiddleware(TransportMiddleware middleware) {
        this.middleware = middleware;
    }

    /**
     * Zapíše paket do výstupního proudu.
     *
     * @param packet paket k zápisu
     * @throws Exception pokud dojde k chybě při zápisu
     */
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
     * Přečte paket ze vstupního proudu.
     *
     * @return přečtený paket nebo null, pokud je proud uzavřen
     * @throws IOException pokud dojde k chybě při čtení
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

    /**
     * Přečte všechna data ze vstupního proudu.
     *
     * @return přečtená data
     * @throws IOException pokud dojde k chybě při čtení
     */
    public byte[] readAll() throws IOException {
        return input.readAllBytes();
    }

    /**
     * Přečte specifikovaný počet bajtů ze vstupního proudu.
     *
     * @param len počet bajtů k přečtení
     * @return přečtené bajty
     * @throws IOException pokud dojde k chybě při čtení
     */
    public byte[] readN(int len) throws IOException {
        return input.readNBytes(len);
    }

    /**
     * Vyprázdní výstupní proud.
     *
     * @throws IOException pokud dojde k chybě při vyprázdnění
     */
    public void flush() throws IOException {
        output.flush();
    }
}