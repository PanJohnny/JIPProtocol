package me.panjohnny.jip.security;

import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.transport.TransportMiddleware;
import me.panjohnny.jip.util.AESUtil;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public sealed class SecurityLayer implements TransportMiddleware permits ClientSecurityLayer, ServerSecurityLayer {

    private SecretKey aesKey;
    private final static System.Logger logger = System.getLogger(SecurityLayer.class.getName());

    protected byte[] encryptRSA(byte[] data, byte[] key) throws SecureTransportException {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(spec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new SecureTransportException("Failed to encrypt data with RSA: " + e.getMessage());
        }
    }

    protected PublicKey createPublicKey(byte[] publicKeyBytes)
            throws SecureTransportException {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new SecureTransportException("Failed to create public key: " + e.getMessage());
        }
    }

    protected SecretKey generateAESKey() throws SecureTransportException {
        try {
            this.aesKey = AESUtil.generateAESKey();
        } catch (NoSuchAlgorithmException e) {
            throw new SecureTransportException("Failed to generate AES key: " + e.getMessage());
        }
        return this.aesKey;
    }

    protected void setAESKey(byte[] key) {
        // Create SecretKey class from byte array
        this.aesKey = new SecretKeySpec(key, 0, key.length, "AES");
    }

    public byte[] encrypt(byte[] data) throws SecureTransportException {
        try {
            return AESUtil.encryptAES(data, aesKey);
        } catch (Exception e) {
            throw new SecureTransportException("Failed to encrypt data with AES: " + e.getMessage());
        }
    }

    public byte[] decrypt(byte[] encryptedData) throws SecureTransportException {
        try {
            return AESUtil.decryptAES(encryptedData, aesKey);
        } catch (Exception e) {
            throw new SecureTransportException("Failed to decrypt data with AES: " + e.getMessage());
        }

    }

    @Override
    public Packet proccessWrite(Packet packet) {
        try {
            return packet.encryptData(this);
        } catch (SecureTransportException e) {
            logger.log(System.Logger.Level.ERROR, "Failed to encrypt packet: " + e.getMessage());
            return packet;
        }    
    }

    @Override
    public Packet proccessRead(Packet packet) {
        try {
            return packet.decryptData(this);
        } catch (SecureTransportException e) {
            logger.log(System.Logger.Level.ERROR, "Failed to decrypt packet: " + e.getMessage());
            return packet;
        }
    }
}