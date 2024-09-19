package me.panjohnny.jip.commons;

import java.util.HashMap;

import me.panjohnny.jip.transport.Packet;

public class Response extends Packet {
    private final String version;
    private final HashMap<String, String> headers;
    private final byte[] body;

    public Response(String version, HashMap<String, String> headers, byte[] body) {
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public Response(String version, String status) {
        this.version = version;
        this.headers = new HashMap<>();
        this.headers.put("Status", status);
        this.body = new byte[0];
    }

    public String getVersion() {
        return version;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    /**
     * Scheme:
     * 4 bytes - length of packet
     * data
     * 
     * Data scheme:
     * JIP/1.0
     * header1: value1
     * header2: value2
     * \r\n
     * body
     */
    public byte[] serialize() {
        useData(toString().getBytes());
        return super.serialize();
    }

    @Override
    public String toString() {
        var data = new StringBuilder();
        data.append(version).append("\n");
        headers.forEach((key, value) -> data.append(key).append(": ").append(value).append("\n"));
        data.append("\r\n");
        data.append(new String(body));
        return data.toString();
    }

    public static Response parse(Packet packet) {
        var data = new String(packet.getData());
        var lines = data.split("\n");
        var version = lines[0];
        var headers = new HashMap<String, String>();
        var body = new byte[0];
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].equals("\r")) {
                body = data.substring(i + 1).getBytes();
                break;
            }
            var header = lines[i].split(": ");
            headers.put(header[0], header[1]);
        }
        return new Response(version, headers, body);
    }
}
