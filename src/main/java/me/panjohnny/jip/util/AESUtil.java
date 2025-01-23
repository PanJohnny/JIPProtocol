package me.panjohnny.jip.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESUtil {
    public static final int IV_LENGTH = 16;
    // With added padding for == at the end
    public static final int IV_LENGTH_BASE64 = (int) Math.ceil(4 * (IV_LENGTH/3.0)) + 2;
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // AES-256
        return keyGen.generateKey();
    }

    /**
     * Encrypts data using AES encryption with a given key.
     * @param data data to encrypt
     * @param key key to use for encryption
     * @return a bytepair containing the IV and the encrypted data
     * @throws Exception if encryption fails
     */
    public static Bytes encryptAES(Bytes data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");
        // Generate a random IV (Initialization Vector)
        byte[] iv = new byte[cipher.getBlockSize()];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);

        //ByteBuffer.allocate(120).put();


        for (int i = 0; i < data.bytes().length; i++) {
            data.bytes()[i] = cipher.update(data.bytes()[i]);
        }

        cipher.doFinal();

        return data.prepend(iv);
    }

    public static byte[] decryptAES(byte[] encryptedData, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
        return cipher.doFinal(encryptedData);
    }

}