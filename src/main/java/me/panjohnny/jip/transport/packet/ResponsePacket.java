package me.panjohnny.jip.transport.packet;

import me.panjohnny.jip.commons.StatusCode;
import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.util.Bytes;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Paket reprezentující odpověď ze serveru. Pakety jsou neměnné.
 * <p>
 * Tento paket obsahuje verzi protokolu, stavový kód, hlavičky a tělo odpovědi.
 * </p>
 *
 * @see Packet
 * @see me.panjohnny.jip.transport.TransportLayer
 * @see me.panjohnny.jip.commons.Response
 * @since 1.0
 */
public class ResponsePacket extends Packet {
    private final String version;
    private final Map<String, String> headers;
    private final byte[] body;
    private final String status;

    /**
     * Vytvoří nový ResponsePacket se specifikovanou verzí, stavem, hlavičkami a tělem.
     *
     * @param version verze odpovědi
     * @param status  stavový kód odpovědi
     * @param headers hlavičky odpovědi
     * @param body    tělo odpovědi
     */
    public ResponsePacket(String version, String status, Map<String, String> headers, byte[] body) {
        this.version = version;
        if (headers != null)
            this.headers = Collections.unmodifiableMap(headers);
        else
            this.headers = null;
        this.body = body;
        this.status = status;
    }

    /**
     * @return verze odpovědi
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return hlavičky odpovědi
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @return tělo odpovědi
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * @return stavový kód odpovědi
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return stavový kód odpovědi jako StatusCode
     */
    public StatusCode getStatusCode() {
        return StatusCode.getFromCode(status);
    }

    /**
     * Připraví paket pro přenos.
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
        sb.append(version).append(" ").append(status).append("\n");
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
     * Převede paket na ResponsePacket.
     *
     * @param packet paket na převedení
     * @return převedený paket
     */
    public static ResponsePacket parse(Packet packet) {
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
        String[] versionStatus = lines[0].split(" ", 2);
        String version = versionStatus[0];
        String status = versionStatus[1].trim();
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

        return new ResponsePacket(version, status, headers, body);
    }
}