package me.panjohnny.jip.commons;

import me.panjohnny.jip.transport.packet.RequestPacket;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Reprezentuje žádost na server.
 * <p>
 * Tato třída slouží k vytvoření žádosti, která bude odeslána na server.
 * Žádost obsahuje verzi protokolu, cestu k požadovanému zdroji, hlavičky a tělo žádosti.
 * </p>
 *
 * @author Jan Štefanča
 * @see JIPVersion
 * @see RequestPacket
 * @since 1.0
 */
public class Request extends PacketFactory<RequestPacket> {
    private String version;
    private String resource;
    private HashMap<String, String> headers;
    private byte[] body;

    /**
     * Vytvoří novou žádost s danou verzí a cestou.
     *
     * @param version  verze protokolu, viz {@link JIPVersion}
     * @param resource cesta k požadovanému zdroji
     */
    public Request(String version, String resource) {
        this.version = version;
        this.resource = resource;
        this.headers = new HashMap<>();
    }

    /**
     * Vytvoří novou žádost s danou verzí a cestou.
     *
     * @param version  verze protokolu, viz {@link JIPVersion}
     * @param resource cesta k požadovanému zdroji
     */
    public Request(JIPVersion version, String resource) {
        this.version = version.toString();
        this.resource = resource;
        this.headers = new HashMap<>();
    }

    /**
     * Získá verzi protokolu.
     *
     * @return verze protokolu
     */
    public String getVersion() {
        return version;
    }

    /**
     * Nastaví verzi protokolu.
     *
     * @param version verze protokolu
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Získá cestu k požadovanému zdroji.
     *
     * @return cesta k požadovanému zdroji
     */
    public String getResource() {
        return resource;
    }

    /**
     * Nastaví cestu k požadovanému zdroji.
     *
     * @param resource cesta k požadovanému zdroji
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * Získá hlavičky žádosti.
     *
     * @return hlavičky žádosti
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Nastaví hlavičky žádosti.
     *
     * @param headers hlavičky žádosti
     */
    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Nastaví hlavičku žádosti.
     *
     * @param key   klíč hlavičky
     * @param value hodnota hlavičky
     */
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    /**
     * Získá tělo žádosti.
     *
     * @return tělo žádosti
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Nastaví tělo žádosti.
     *
     * @param body tělo žádosti
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * Nastaví tělo žádosti.
     *
     * @param body tělo žádosti jako řetězec
     */
    public void setBody(String body) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Vytvoří paket žádosti.
     *
     * @return paket žádosti
     */
    @Override
    public RequestPacket fabricate() {
        return new RequestPacket(version, resource, headers, body);
    }
}