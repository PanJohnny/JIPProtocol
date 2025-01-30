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
 * A packet representing response sent from the server. Please note that packets are immutable.
 *
 * @author Jan Štefanča
 * @see Packet
 * @see me.panjohnny.jip.transport.TransportLayer
 * @see me.panjohnny.jip.commons.Response
 */
public class ResponsePacket extends Packet {
    private final String version;
    private final Map<String, String> headers;
    private final byte[] body;
    private final String status;

    public ResponsePacket(String version, String status, Map<String, String> headers, byte[] body) {
        this.version = version;
        if (headers != null)
            this.headers = Collections.unmodifiableMap(headers);
        else
            this.headers = null;
        this.body = body;
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getStatus() {
        return status;
    }

    public StatusCode getStatusCode() {
        return StatusCode.getFromCode(status);
    }

    @Override
    public void prepare() {
        useData(toBytes());
    }

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


    public static ResponsePacket parse(Packet packet) {
        byte[] data = packet.getData().at(0);
        int headerEndIndex = -1;

        // Find the end of the headers section
        for (int i = 0; i < data.length; i++) {
            if (data[i] == '\r' && data[i + 1] == '\n') {
                headerEndIndex = i + 2;
                break;
            }
        }

        if (headerEndIndex == -1) {
            throw new IllegalArgumentException("Invalid packet format: no header end found");
        }

        // Extract headers
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

        // Extract body
        byte[] body = Arrays.copyOfRange(data, headerEndIndex, data.length);

        return new ResponsePacket(version, status, headers, body);
    }
}
