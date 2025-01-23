package me.panjohnny.jip.commons;

import me.panjohnny.jip.transport.packet.ResponsePacket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Response extends PacketFactory<ResponsePacket> {
    private final String status;
    private String version;
    private HashMap<String, String> headers;
    private byte[] body;

    // public static final Response DEFAULT = new Response(JIPVersion.getDefault().toString(), StatusCodes.OK.toString());
    // public static final ResponsePacket NOT_FOUND = new ResponsePacket(JIPVersion.getDefault().toString(), StatusCodes.NOT_FOUND.toString(), null, null);

    /**
     * Creates a new response with given version and status.
     * @param version see {@link JIPVersion}
     * @param status see {@link StatusCodes}
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

    /**
     * Sets the body to the given string
     * @param s string that will become the body
     */
    public void sendString(String s) {
        this.body = s.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Reads contents of the file and sets the body to them
     * @param path path of the file
     * @throws IOException if an I/ O error occurs reading from the stream
     */
    public void sendFile(String path) throws IOException {
        this.body = Files.readAllBytes(Path.of(path));
    }

    @Override
    public ResponsePacket fabricate() {
        return new ResponsePacket(version, status, headers, body);
    }
}
