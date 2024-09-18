package me.panjohnny.jip.security;

import me.panjohnny.jip.util.AESUtil;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public sealed class SecurityLayer permits ClientSecurityLayer, ServerSecurityLayer {

    private SecretKey aesKey;

    protected byte[] encryptRSA(byte[] data, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    protected PublicKey createPublicKey(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    protected SecretKey generateAESKey() throws NoSuchAlgorithmException {
        this.aesKey = AESUtil.generateAESKey();
        return this.aesKey;
    }

    protected void setAESKey(byte[] key) {
        // Create SecretKey class from byte array
        this.aesKey = new SecretKeySpec(key, 0, key.length, "AES");
    }

    public byte[] encrypt(byte[] data) throws Exception {
        return AESUtil.encryptAES(data, aesKey);
    }

    public byte[] decrypt(byte[] encryptedData) throws Exception {
        return AESUtil.decryptAES(encryptedData, aesKey);
    }

    protected byte[] createConfirmSEQ() {
        byte[] confirmSEQ = new byte[2];
        new SecureRandom().nextBytes(confirmSEQ);
        return confirmSEQ;
    }
}