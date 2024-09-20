package me.panjohnny.jip.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESUtil {
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // AES-256
        return keyGen.generateKey();
    }

    public static byte[] encryptAES(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // Generate a random IV (Initialization Vector)
        byte[] iv = new byte[cipher.getBlockSize()];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);

        // Prepend IV to the encrypted data for use in decryption
        byte[] encryptedData = cipher.doFinal(data);
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
        return combined;
    }

    public static byte[] decryptAES(byte[] encryptedDataWithIv, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Extract the IV from the beginning of the encrypted data
        byte[] iv = Arrays.copyOfRange(encryptedDataWithIv, 0, cipher.getBlockSize());
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        // Extract the actual encrypted data
        byte[] encryptedData = Arrays.copyOfRange(encryptedDataWithIv, cipher.getBlockSize(),
                encryptedDataWithIv.length);

        cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
        return cipher.doFinal(encryptedData);
    }

}