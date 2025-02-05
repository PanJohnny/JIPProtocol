package me.panjohnny.jip.transport;

import me.panjohnny.jip.security.SecureTransportException;
import me.panjohnny.jip.security.SecurityLayer;
import me.panjohnny.jip.util.ByteUtil;
import me.panjohnny.jip.util.Bytes;

import java.io.InputStream;
import java.util.Arrays;

/**
 * Reprezentuje paket, ten je jednotkou přenosu mezi klientem a serverem.
 * <p>
 * Paket může obsahovat data nebo být spojen s datovým proudem.
 * </p>
 * <p>
 * Třída poskytuje metody pro přípravu, šifrování a dešifrování dat.
 * </p>
 *
 * @author Jan Štefanča
 * @see me.panjohnny.jip.transport.packet.HandshakePacket
 * @see me.panjohnny.jip.transport.packet.ResponsePacket
 * @see me.panjohnny.jip.transport.packet.RequestPacket
 * @since 1.0
 */
public class Packet {
    public byte[] length;
    protected Bytes data;
    protected InputStream stream;
    protected long streamLen = 0;

    /**
     * Vytvoří nový paket se specifikovanou délkou a daty.
     *
     * @param length délka dat
     * @param data   data paketu
     */
    public Packet(byte[] length, byte[] data) {
        this.length = length;
        this.data = new Bytes(data);
    }

    /**
     * Vytvoří nový paket se specifikovanou délkou a daty.
     *
     * @param length délka dat
     * @param data   data paketu
     */
    public Packet(int length, byte[] data) {
        this.length = ByteUtil.intToByteArray4(length); // max length is 2^32 - 1
        this.data = new Bytes(data);
    }

    /**
     * Vytvoří nový prázdný paket.
     */
    public Packet() {
        length = new byte[4];
        this.data = new Bytes();
    }

    /**
     * Nastaví délku paketu.
     *
     * @param length délka paketu
     */
    public void setLength(int length) {
        this.length = ByteUtil.intToByteArray4(length);
    }

    /**
     * Vrátí délku paketu.
     *
     * @return délka paketu
     */
    public int getLength() {
        return ByteUtil.byteArray4ToInt(length);
    }

    /**
     * Použije data pro tento paket.
     *
     * @param data data paketu
     */
    public void useData(Bytes data) {
        this.data = data;
        updateLen();
    }

    /**
     * Aktualizuje délku paketu na základě dat a délky proudu.
     */
    public void updateLen() {
        setLength((int) (data.length() + streamLen));
    }

    /**
     * Vrátí data paketu.
     *
     * @return data paketu
     */
    public Bytes getData() {
        return data;
    }

    /**
     * Spojí datový proud s tímto paketem. Proud je poté zašifrován a odeslán jako data.
     *
     * @param stream datový proud ke spojení s tímto paketem
     * @param len    délka dat, která mají být přečtena z proudu
     */
    public void connectStream(InputStream stream, long len) {
        this.stream = stream;
        this.streamLen = len;
        updateLen();
    }

    /**
     * Vrátí spojený datový proud.
     *
     * @return spojený datový proud
     */
    public InputStream getConnectedStream() {
        return stream;
    }

    /**
     * Vrátí délku spojeného datového proudu.
     *
     * @return délka spojeného datového proudu
     */
    public long getStreamLen() {
        return streamLen;
    }

    /**
     * Zjistí, zda je spojený datový proud.
     *
     * @return true, pokud je spojený datový proud, jinak false
     */
    public boolean hasConnectedStream() {
        return getConnectedStream() != null;
    }

    /**
     * Slouží k přípravě paketu.
     */
    public void prepare() {
        // Do nothing by default
    }

    /**
     * Smaže všechna data.
     */
    public void free() {
        data.clear();
        length = null;
    }

    /**
     * Zašifruje paket pomocí bezpečnostní vrstvy.
     *
     * @param securityLayer bezpečnostní vrstva
     * @return zašifrovaný paket (aktuální instance)
     * @throws SecureTransportException pokud dojde k chybě při šifrování
     */
    public Packet encryptData(SecurityLayer securityLayer) throws SecureTransportException {
        useData(securityLayer.encrypt(data));
        return this;
    }

    /**
     * Dešifruje paket pomocí bezpečnostní vrstvy.
     *
     * @param securityLayer bezpečnostní vrstva
     * @return dešifrovaný paket (aktuální instance)
     * @throws SecureTransportException pokud dojde k chybě při dešifrování
     */
    public Packet decryptData(SecurityLayer securityLayer) throws SecureTransportException {
        var arr = data.at(0);
        arr = securityLayer.decrypt(arr);
        useData(new Bytes(arr));
        return this;
    }
}