package me.panjohnny.jip.commons;

import me.panjohnny.jip.transport.packet.ResponsePacket;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Response extends PacketFactory<ResponsePacket> {
    private String status;
    private String version;
    private HashMap<String, String> headers;
    private byte[] body;
    private InputStream stream;
    private long streamLen = 0;

    // public static final Response DEFAULT = new Response(JIPVersion.getDefault().toString(), StatusCodes.OK.toString());
    // public static final ResponsePacket NOT_FOUND = new ResponsePacket(JIPVersion.getDefault().toString(), StatusCodes.NOT_FOUND.toString(), null, null);

    /**
     * Creates a new response with given version and status.
     * @param version see {@link JIPVersion}
     * @param status see {@link StatusCode}
     */
    public Response(String version, String status) {
        this.version = version;
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(StatusCode code) {
        this.status = code.toString();
    }

    public String getStatus() {
        return status;
    }

    /**
     * Sets the body to the given string
     * @param s string that will become the body
     */
    public void sendString(String s) {
        this.body = s.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Reads contents of the file and sets the body to them
     * @param file the file to be sent
     */
    public void sendFile(File file) throws FileNotFoundException {
        stream = new FileInputStream(file);
        streamLen = file.length();
    }

    @Override
    public ResponsePacket fabricate() {
        var p = new ResponsePacket(version, status, headers, body);
        if (stream != null) {
            p.connectStream(stream, streamLen);
        }
        return p;
    }
}
