package me.panjohnny.jip.transport.packet;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.util.Bytes;

/**
 * A packet representing response sent from the server.
 *
 * @author Jan Štefanča
 * @see Packet
 * @see me.panjohnny.jip.transport.TransportLayer
 * @see me.panjohnny.jip.commons.Response
 * @implNote Please note that packets are immutable.
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
        var data = new String(packet.getData().at(0), StandardCharsets.UTF_8);
        var lines = data.split("\n");
        var versionStatus = lines[0].replace("\r", "").split(" ", 2);
        var version = versionStatus[0];
        var status = versionStatus[1];
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
        return new ResponsePacket(version, status, headers, body.toString().getBytes(StandardCharsets.UTF_8));
    }
}
