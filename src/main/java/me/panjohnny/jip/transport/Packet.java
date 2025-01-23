package me.panjohnny.jip.transport;

import me.panjohnny.jip.security.SecureTransportException;
import me.panjohnny.jip.security.SecurityLayer;
import me.panjohnny.jip.util.ByteUtil;
import me.panjohnny.jip.util.Bytes;

import java.util.Arrays;

public class Packet {
    public byte[] length;
    protected Bytes data;

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
        setLength(data.length());
    }

    public Bytes getData() {
        return data;
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
