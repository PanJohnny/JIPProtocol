package me.panjohnny.jip.transport;

import me.panjohnny.jip.security.SecureTransportException;
import me.panjohnny.jip.security.SecurityLayer;
import me.panjohnny.jip.util.ByteUtil;
import me.panjohnny.jip.util.Bytes;

import java.io.InputStream;
import java.util.Arrays;

public class Packet {
    public byte[] length;
    protected Bytes data;
    protected InputStream stream;
    protected long streamLen = 0;

    public Packet(byte[] length, byte[] data) {
        this.length = length;
        this.data = new Bytes(data);
    }

    public Packet(int length, byte[] data) {
        this.length = ByteUtil.intToByteArray4(length); // max length is 2^32 - 1
        this.data = new Bytes(data);
    }

    public Packet() {
        length = new byte[4];
        this.data = new Bytes();
    }

    public void setLength(int length) {
        this.length = ByteUtil.intToByteArray4(length);
    }

    public int getLength() {
        return ByteUtil.byteArray4ToInt(length);
    }

    public void useData(Bytes data) {
        this.data = data;
        updateLen();
    }

    public void updateLen() {
        setLength((int) (data.length() + streamLen));
    }

    public Bytes getData() {
        return data;
    }

    /**
     * Connects a stream to this data socket. The stream is then encrypted and sent as the data.
     * @param stream stream to connect to this packet
     * @param len the amount of data to be read from the stream
     */
    public void connectStream(InputStream stream, long len) {
        this.stream = stream;
        this.streamLen = len;
        updateLen();
    }

    public InputStream getConnectedStream() {
        return stream;
    }

    public long getStreamLen() {
        return streamLen;
    }

    public boolean hasConnectedStream() {
        return getConnectedStream() != null;
    }

    public void prepare() {
        // Do nothing by default
    }

    public void free() {
        data.clear();
        length = null;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "length=" + Arrays.toString(length) +
                ", data=" + data +
                '}';
    }

    public Packet encryptData(SecurityLayer securityLayer) throws SecureTransportException {
        useData(securityLayer.encrypt(data));
        return this;
    }

    public Packet decryptData(SecurityLayer securityLayer) throws SecureTransportException {
        var arr = data.at(0);
        arr = securityLayer.decrypt(arr);
        useData(new Bytes(arr));
        return this;
    }
}
