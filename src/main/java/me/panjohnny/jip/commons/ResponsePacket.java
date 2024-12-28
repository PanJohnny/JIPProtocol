package me.panjohnny.jip.commons;

import java.util.HashMap;

import me.panjohnny.jip.transport.Packet;

/**
 * A packet representing response sent from the server.
 *
 * @author Jan Štefanča
 * @see Packet
 * @see me.panjohnny.jip.transport.TransportLayer
 */
public class ResponsePacket extends Packet {
    private final String version;
    private final HashMap<String, String> headers;
    private final byte[] body;

    public static final ResponsePacket OK = new ResponsePacket("JIP/1.0", "OK");

    public ResponsePacket(String version, HashMap<String, String> headers, byte[] body) {
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public ResponsePacket(String version, String status) {
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

    @Override
    public void prepare() {
        useData(toString().getBytes());
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

    public static ResponsePacket parse(Packet packet) {
        var data = new String(packet.getData());
        var lines = data.split("\n");
        var version = lines[0];
        var headers = new HashMap<String, String>();
        StringBuilder body = new StringBuilder();
        boolean isBody = false;
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.equals("\r")) {
                // The rest of the lines after this one is the request body
                isBody = true;
            } else if(isBody) {
                body.append(line).append("\n");
            } else {
                var header = line.split(": ");
                headers.put(header[0], header[1]);
            }
        }
        return new ResponsePacket(version, headers, body.toString().getBytes());
    }
}
