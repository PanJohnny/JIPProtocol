package me.panjohnny.jip.commons;

import java.util.HashMap;

import me.panjohnny.jip.transport.Packet;

/**
 * A packet representing request to the server.
 *
 * @author Jan Štefanča
 * @see Packet
 * @see me.panjohnny.jip.transport.TransportLayer
 */
public class RequestPacket extends Packet {
    private final String version;
    private final String resource;
    private final HashMap<String, String> headers;
    private final byte[] body;

    public RequestPacket(String version, String resource, HashMap<String, String> headers, byte[] body) {
        super();
        this.version = version;
        this.resource = resource;
        this.headers = headers;
        this.body = body;
    }

    public RequestPacket(String version, String resource, byte[] body) {
        super();
        this.version = version;
        this.resource = resource;
        this.headers = new HashMap<>();
        this.body = body;
    }

    public RequestPacket(String version, String resource) {
        super();
        this.version = version;
        this.resource = resource;
        this.headers = new HashMap<>();
        this.body = new byte[0];
    }

    public String getVersion() {
        return version;
    }

    public String getResource() {
        return resource;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public void prepare() {
        useData(toByteArray());
    }

    public byte[] toByteArray() {
        StringBuilder sb = new StringBuilder();
        sb.append(version).append(" ").append(resource).append("\n");
        for (String key : headers.keySet()) {
            sb.append(key).append(": ").append(headers.get(key)).append("\n");
        }
        sb.append("\r\n");
        byte[] headerBytes = sb.toString().getBytes();
        byte[] data = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, data, 0, headerBytes.length);
        System.arraycopy(body, 0, data, headerBytes.length, body.length);
        return data;
    }

    public static RequestPacket parse(Packet packet) {
        String data = new String(packet.getData());
        String[] lines = data.split("\n");
        String[] versionResource = lines[0].split(" ", 2);
        String version = versionResource[0];
        String resource = versionResource[1].trim();
        HashMap<String, String> headers = new HashMap<>();
        byte[] body = new byte[0];
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].equals("\r")) {
                body = data.substring(i + 1).getBytes();
                break;
            }
            String[] header = lines[i].split(":", 2);
            headers.put(header[0].trim(), header[1].trim());
        }
        return new RequestPacket(version, resource, headers, body);
    }
}
