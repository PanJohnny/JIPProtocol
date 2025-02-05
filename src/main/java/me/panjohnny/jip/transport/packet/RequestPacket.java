package me.panjohnny.jip.transport.packet;

import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.util.Bytes;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Paket představující požadavek. Pakety jsou neměnné!
 *
 * @author Jan Štefanča
 * @see Packet
 * @see me.panjohnny.jip.transport.TransportLayer
 * @see me.panjohnny.jip.commons.Request
 * @since 1.0
 */
public class RequestPacket extends Packet {
    private final String version;
    private final String resource;
    private final Map<String, String> headers;
    private final byte[] body;

    /**
     * Vytvoří nový RequestPacket se specifikovanou verzí, zdrojem, hlavičkami a tělem.
     *
     * @param version  verze požadavku
     * @param resource zdroj požadavku
     * @param headers  hlavičky požadavku
     * @param body     tělo požadavku
     */
    public RequestPacket(String version, String resource, Map<String, String> headers, byte[] body) {
        super();
        this.version = version;
        this.resource = resource;
        this.headers = Collections.unmodifiableMap(headers);
        this.body = body;
    }

    /**
     * Vytvoří nový RequestPacket se specifikovanou verzí, zdrojem a tělem.
     *
     * @param version  verze požadavku
     * @param resource zdroj požadavku
     * @param body     tělo požadavku
     */
    public RequestPacket(String version, String resource, byte[] body) {
        super();
        this.version = version;
        this.resource = resource;
        this.headers = null;
        this.body = body;
    }

    /**
     * Vytvoří nový RequestPacket se specifikovanou verzí a zdrojem.
     *
     * @param version  verze požadavku
     * @param resource zdroj požadavku
     */
    public RequestPacket(String version, String resource) {
        super();
        this.version = version;
        this.resource = resource;
        this.headers = null;
        this.body = new byte[0];
    }

    /**
     * @return verze žádosti
     * @see me.panjohnny.jip.commons.JIPVersion
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return cesta žádosti
     */
    public String getResource() {
        return resource;
    }

    /**
     * @return hlavičky žádosti
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @return tělo žádosti
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Připraví paket pro přenos
     */
    @Override
    public void prepare() {
        useData(toBytes());
    }

    /**
     * Konvertuje paket na bajty.
     *
     * @return bajty
     */
    public Bytes toBytes() {
        StringBuilder sb = new StringBuilder();
        sb.append(version).append(" ").append(resource).append("\n");
        if (headers != null) {
            for (String key : headers.keySet()) {
                sb.append(key).append(": ").append(headers.get(key)).append("\n");
            }
        }
        sb.append("\r\n");
        byte[] headerBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        if (body == null || body.length == 0) {
            return new Bytes(headerBytes);
        }
        return new Bytes(headerBytes, body);
    }

    /**
     * Převede paket na RequestPacket
     *
     * @param packet paket na převedení
     * @return převedený paket
     */
    public static RequestPacket parse(Packet packet) {
        byte[] data = packet.getData().at(0);
        int headerEndIndex = -1;

        // Najde konec sekce hlaviček
        for (int i = 0; i < data.length; i++) {
            if (data[i] == '\r' && data[i + 1] == '\n') {
                headerEndIndex = i + 2;
                break;
            }
        }

        if (headerEndIndex == -1) {
            throw new IllegalArgumentException("Neplatný formát paketu: nebyl nalezen konec hlavičky");
        }

        // Extrahuje hlavičky
        String headersString = new String(data, 0, headerEndIndex, StandardCharsets.UTF_8);
        String[] lines = headersString.split("\r\n");
        String[] versionResource = lines[0].split(" ", 2);
        String version = versionResource[0];
        String resource = versionResource[1].trim();
        HashMap<String, String> headers = new HashMap<>();

        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                break;
            }
            String[] header = lines[i].split(":", 2);
            headers.put(header[0].trim(), header[1].trim());
        }

        // Extrahuje tělo
        byte[] body = Arrays.copyOfRange(data, headerEndIndex, data.length);

        return new RequestPacket(version, resource, headers, body);
    }
}