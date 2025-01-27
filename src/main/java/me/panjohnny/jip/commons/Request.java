package me.panjohnny.jip.commons;

import me.panjohnny.jip.transport.packet.RequestPacket;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Request extends PacketFactory<RequestPacket> {
    private String version;
    private String resource;
    private HashMap<String, String> headers;
    private byte[] body;

    /**
     * Creates a new request with given version and resource.
     * @param version see {@link JIPVersion}
     * @param resource a path to the requested resource
     */
    public Request(String version, String resource) {
        this.version = version;
        this.resource = resource;
        this.headers = new HashMap<>();
    }

    public Request(JIPVersion version, String resource) {
        this.version = version.toString();
        this.resource = resource;
        this.headers = new HashMap<>();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setBody(String body) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public RequestPacket fabricate() {
        return new RequestPacket(version, resource, headers, body);
    }
}
