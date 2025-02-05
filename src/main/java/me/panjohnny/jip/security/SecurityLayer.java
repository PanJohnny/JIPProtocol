package me.panjohnny.jip.security;

import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.transport.TransportMiddleware;
import me.panjohnny.jip.util.AESUtil;
import me.panjohnny.jip.util.Bytes;
import me.panjohnny.jip.util.IOProcessor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * Vrstva pro šifrování a dešifrování paketů. Funguje jako middleware.
 *
 * @author Jan Štefanča
 * @see Packet
 * @see me.panjohnny.jip.transport.TransportLayer
 * @see TransportMiddleware
 * @since 1.0
 */
public sealed class SecurityLayer implements TransportMiddleware permits ClientSecurityLayer, ServerSecurityLayer {
    private SecretKey aesKey;
    private final static System.Logger LOGGER = System.getLogger(SecurityLayer.class.getName());

    /**
     * Šifruje data pomocí RSA.
     *
     * @param data data k šifrování
     * @param key  veřejný klíč pro šifrování
     * @return zašifrovaná data
     * @throws SecureTransportException pokud se nepodaří data zašifrovat
     */
    protected byte[] encryptRSA(byte[] data, byte[] key) throws SecureTransportException {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(spec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new SecureTransportException("Nepodařilo se zašifrovat data pomocí RSA: " + e.getMessage());
        }
    }

    /**
     * Generuje AES klíč.
     *
     * @return vygenerovaný AES klíč
     * @throws SecureTransportException pokud se nepodaří vygenerovat AES klíč
     */
    protected SecretKey generateAESKey() throws SecureTransportException {
        try {
            this.aesKey = AESUtil.generateAESKey();
        } catch (NoSuchAlgorithmException e) {
            throw new SecureTransportException("Nepodařilo se vygenerovat AES klíč: " + e.getMessage());
        }
        return this.aesKey;
    }

    /**
     * Nastaví AES klíč.
     *
     * @param key AES klíč jako pole bajtů
     */
    protected void setAESKey(byte[] key) {
        this.aesKey = new SecretKeySpec(key, 0, key.length, "AES");
    }

    /**
     * Šifruje data pomocí AES.
     *
     * @param data data k šifrování
     * @return zašifrovaná data
     * @throws SecureTransportException pokud se nepodaří data zašifrovat
     */
    public Bytes encrypt(Bytes data) throws SecureTransportException {
        try {
            return AESUtil.encryptAES(data, aesKey);
        } catch (Exception e) {
            throw new SecureTransportException("Nepodařilo se zašifrovat data pomocí AES: " + e.getMessage(), e);
        }
    }

    /**
     * Dešifruje data pomocí AES.
     *
     * @param encryptedData zašifrovaná data
     * @return dešifrovaná data
     * @throws SecureTransportException pokud se nepodaří data dešifrovat
     */
    public byte[] decrypt(byte[] encryptedData) throws SecureTransportException {
        try {
            ByteBuffer iv = ByteBuffer.allocate(AESUtil.IV_LENGTH);
            iv.put(encryptedData, 0, AESUtil.IV_LENGTH);
            var ivDec = iv.array();

            ByteBuffer encrypted = ByteBuffer.allocate(encryptedData.length - AESUtil.IV_LENGTH);
            for (int i = AESUtil.IV_LENGTH; i < encryptedData.length; i++) {
                encrypted.put(encryptedData[i]);
            }
            var encryptedDataDec = encrypted.array();

            return AESUtil.decryptAES(encryptedDataDec, aesKey, ivDec);
        } catch (Exception e) {
            throw new SecureTransportException("Nepodařilo se dešifrovat data pomocí AES: " + e.getMessage(), e);
        }
    }

    /**
     * Zpracuje zápis paketu a zašifruje jeho data.
     *
     * @param packet paket k zpracování
     * @return zašifrovaný paket
     */
    @Override
    public Packet processWrite(Packet packet) {
        try {
            return packet.encryptData(this);
        } catch (SecureTransportException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Nepodařilo se zašifrovat paket: " + e.getMessage(), e);
            return packet;
        }
    }

    /**
     * Zpracuje čtení paketu a dešifruje jeho data.
     *
     * @param packet paket k zpracování
     * @return dešifrovaný paket
     */
    @Override
    public Packet processRead(Packet packet) {
        try {
            return packet.decryptData(this);
        } catch (SecureTransportException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Nepodařilo se dešifrovat paket: " + e.getMessage(), e);
            return packet;
        }
    }

    /**
     * Zpracuje vstupně-výstupní operace paketu.
     *
     * @param packet paket k zpracování
     * @return zpracovaný IOProcessor
     * @throws Exception pokud se nepodaří zpracovat IO
     */
    @Override
    public IOProcessor processIO(Packet packet) throws Exception {
        return AESUtil.encryptStream(packet.getData(), this.aesKey, packet.getStreamLen());
    }
}