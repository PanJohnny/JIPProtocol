package me.panjohnny.jip.security;

import me.panjohnny.jip.transport.Packet;
import me.panjohnny.jip.transport.TransportMiddleware;
import me.panjohnny.jip.util.AESUtil;
import me.panjohnny.jip.util.Bytes;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * A layer for encrypting and decrypting packets. Works as a middleware.
 *
 * @see Packet
 * @see me.panjohnny.jip.transport.TransportLayer
 * @see TransportMiddleware
 * @author Jan Štefanča
 */
public sealed class SecurityLayer implements TransportMiddleware permits ClientSecurityLayer, ServerSecurityLayer {
    private SecretKey aesKey;
    private final static System.Logger LOGGER = System.getLogger(SecurityLayer.class.getName());

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

    public Bytes encrypt(Bytes data) throws SecureTransportException {
        try {
            // Add Base64 encoding to the encrypted data
            var encryptedData = AESUtil.encryptAES(data, aesKey);
            return encryptedData.base64Encode();
        } catch (Exception e) {
            throw new SecureTransportException("Failed to encrypt data with AES: " + e.getMessage(), e);
        }
    }
    
    public byte[] decrypt(byte[] encryptedData) throws SecureTransportException {
        try {
            // Decode encrypted data for first part (until ==)
            byte prev = 0;
            ByteBuffer iv = ByteBuffer.allocate(AESUtil.IV_LENGTH_BASE64);
            for (byte b : encryptedData) {
                iv.put(b);
                if (b == '=' && prev == '=') {
                    break;
                }
                prev = b;
            }
            // Decode Base64 before decrypting
            var ivDec = Base64.getDecoder().decode(iv.array());
            System.out.println(ivDec.length);

            // Decrypt the rest of the data
            ByteBuffer encrypted = ByteBuffer.allocate(encryptedData.length - AESUtil.IV_LENGTH_BASE64);
            for (int i = AESUtil.IV_LENGTH_BASE64; i < encryptedData.length; i++) {
                encrypted.put(encryptedData[i]);
            }
            var encryptedDataDec = Base64.getDecoder().decode(encrypted.array());
            System.out.println(encryptedDataDec.length);

            return AESUtil.decryptAES(encryptedDataDec, aesKey, ivDec);
        } catch (Exception e) {
            throw new SecureTransportException("Failed to decrypt data with AES: " + e.getMessage(), e);
        }
    }
    

    @Override
    public Packet processWrite(Packet packet) {
        try {
            return packet.encryptData(this);
        } catch (SecureTransportException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to encrypt packet: " + e.getMessage(), e);
            return packet;
        }    
    }

    @Override
    public Packet processRead(Packet packet) {
        try {
            return packet.decryptData(this);
        } catch (SecureTransportException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to decrypt packet: " + e.getMessage(), e);
            return packet;
        }
    }
}