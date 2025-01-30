package me.panjohnny.jip.transport.packet;

import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.util.Bytes;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A packet representing request. Please note that packets are immutable.
 *
 * @author Jan Štefanča
 * @see Packet
 * @see me.panjohnny.jip.transport.TransportLayer
 * @see me.panjohnny.jip.commons.Request
 */
public class RequestPacket extends Packet {
    private final String version;
    private final String resource;
    private final Map<String, String> headers;
    private final byte[] body;

    public RequestPacket(String version, String resource, Map<String, String> headers, byte[] body) {
        super();
        this.version = version;
        this.resource = resource;
        this.headers = Collections.unmodifiableMap(headers);
        this.body = body;
    }

    public RequestPacket(String version, String resource, byte[] body) {
        super();
        this.version = version;
        this.resource = resource;
        this.headers = null;
        this.body = body;
    }

    public RequestPacket(String version, String resource) {
        super();
        this.version = version;
        this.resource = resource;
        this.headers = null;
        this.body = new byte[0];
    }

    public String getVersion() {
        return version;
    }

    public String getResource() {
        return resource;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public void prepare() {
        useData(toBytes());
    }

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

    public static RequestPacket parse(Packet packet) {
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

        // Extract body
        byte[] body = Arrays.copyOfRange(data, headerEndIndex, data.length);

        return new RequestPacket(version, resource, headers, body);
    }
}
