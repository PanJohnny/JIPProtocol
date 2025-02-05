package me.panjohnny.jip.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utilitní třída pro šifrování a dešifrování pomocí AES.
 * <p>
 * Třída poskytuje metody pro generování klíčů, šifrování a dešifrování dat a datových proudů.
 * </p>
 *
 * @author Jan Štefanča
 * @since 1.0
 */
public class AESUtil {
    public static final int IV_LENGTH = 16;

    /**
     * Generuje nový AES klíč.
     *
     * @return vygenerovaný AES klíč
     * @throws NoSuchAlgorithmException pokud algoritmus AES není dostupný
     */
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // AES-256
        return keyGen.generateKey();
    }

    /**
     * Šifruje data pomocí AES šifrování s daným klíčem.
     *
     * @param data data k zašifrování
     * @param key  klíč pro šifrování
     * @return bajty obsahující IV a zašifrovaná data
     * @throws Exception pokud šifrování selže
     */
    public static Bytes encryptAES(Bytes data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");
        byte[] iv = new byte[cipher.getBlockSize()];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);

        for (byte[] chunk : data.bytes()) {
            cipher.update(chunk, 0, chunk.length, chunk, 0);
        }
        cipher.doFinal();

        return data.prepend(iv);
    }

    /**
     * Šifruje datový proud pomocí AES šifrování s daným klíčem.
     *
     * @param data      data k zašifrování
     * @param key       klíč pro šifrování
     * @param streamLen délka datového proudu
     * @return IOProcessor pro zpracování šifrovaného datového proudu
     * @throws Exception pokud šifrování selže
     */
    public static IOProcessor encryptStream(Bytes data, SecretKey key, long streamLen) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");
        byte[] iv = new byte[cipher.getBlockSize()];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        return new IOProcessor() {
            @Override
            public void init(OutputStream out) throws Exception {
                cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
                out.write(iv);

                for (int i = 0; i < data.bytes().length; i++) {
                    out.write(cipher.update(data.bytes()[i]));
                }
            }

            @Override
            public void process(InputStream in, OutputStream out) throws Exception {
                int bytesRead = 0;
                while (bytesRead < streamLen) {
                    byte[] buffer = new byte[cipher.getBlockSize() * 1_000_000];
                    long remaining = streamLen - bytesRead;
                    int toRead = (int) Math.min(buffer.length, remaining);
                    int read = in.read(buffer, 0, toRead);
                    if (read == -1) {
                        break;
                    }
                    out.write(cipher.update(buffer, 0, read));
                    bytesRead += read;
                }
                out.write(cipher.doFinal());
            }
        };
    }

    /**
     * Dešifruje data pomocí AES šifrování s daným klíčem a IV.
     *
     * @param encryptedData zašifrovaná data
     * @param key           klíč pro dešifrování
     * @param iv            inicializační vektor
     * @return dešifrovaná data
     * @throws Exception pokud dešifrování selže
     */
    public static byte[] decryptAES(byte[] encryptedData, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
        return cipher.doFinal(encryptedData);
    }
}