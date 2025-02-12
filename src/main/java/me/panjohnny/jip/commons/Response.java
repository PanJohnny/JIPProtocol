package me.panjohnny.jip.commons;

import me.panjohnny.jip.transport.packet.ResponsePacket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Reprezentuje odpověď ze serveru.
 * <p>
 * Tato třída slouží k vytvoření odpovědi, která bude odeslána ze serveru klientovi.
 * Odpověď obsahuje verzi protokolu, stavový kód, hlavičky a tělo odpovědi.
 * </p>
 *
 * @see JIPVersion
 * @see ResponsePacket
 * @since 1.0
 */
public class Response extends PacketFactory<ResponsePacket> {
    private String status;
    private String version;
    private HashMap<String, String> headers;
    private byte[] body;
    private InputStream stream;
    private long streamLen = 0;

    /**
     * Vytvoří novou odpověď s danou verzí a stavem.
     *
     * @param version verze protokolu, viz {@link JIPVersion}
     * @param status  stavový kód, viz {@link StatusCode}
     */
    public Response(String version, String status) {
        this.version = version;
        this.status = status;
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
     * Získá hlavičky odpovědi.
     *
     * @return hlavičky odpovědi
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Nastaví hlavičky odpovědi.
     *
     * @param headers hlavičky odpovědi
     */
    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Nastaví hlavičku odpovědi.
     *
     * @param key   klíč hlavičky
     * @param value hodnota hlavičky
     */
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    /**
     * Získá tělo odpovědi.
     *
     * @return tělo odpovědi
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Nastaví tělo odpovědi.
     *
     * @param body tělo odpovědi
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * Nastaví tělo odpovědi jako řetězec.
     *
     * @param body tělo odpovědi jako řetězec
     */
    public void sendString(String body) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Nastaví tělo odpovědi jako obsah souboru.
     *
     * @param file soubor, jehož obsah bude tělem odpovědi
     * @throws FileNotFoundException pokud soubor neexistuje
     */
    public void sendFile(File file) throws FileNotFoundException {
        this.stream = new FileInputStream(file);
        this.streamLen = file.length();
    }

    /**
     * Získá stavový kód odpovědi.
     *
     * @return stavový kód odpovědi
     */
    public String getStatus() {
        return status;
    }

    /**
     * Nastaví stavový kód odpovědi.
     *
     * @param status stavový kód odpovědi
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Nastaví stavový kód odpovědi.
     *
     * @param code stavový kód odpovědi, viz {@link StatusCode}
     */
    public void setStatus(StatusCode code) {
        this.status = code.toString();
    }

    /**
     * Vytvoří paket odpovědi.
     *
     * @return paket odpovědi
     */
    @Override
    public ResponsePacket fabricate() {
        var p = new ResponsePacket(version, status, headers, body);
        if (stream != null) {
            p.connectStream(stream, streamLen);
        }
        return p;
    }
}