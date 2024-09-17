package me.panjohnny.jip.protocol;

import me.panjohnny.jip.protocol.packets.ConfirmSEQPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.spec.X509EncodedKeySpec;

public class ServerEncryption {
    private PublicKey publicKey;
    private byte[] confirmSEQ;

    public ServerEncryption(byte[] handshake) {
        byte[] publicKeyBytes = new byte[handshake.length - 2];
        this.confirmSEQ = new byte[2];
        System.arraycopy(handshake, 0, confirmSEQ, 0, 2);
        System.arraycopy(handshake, 2, publicKeyBytes, 0, handshake.length - 2);
        try {
            this.publicKey = createPublicKey(publicKeyBytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private PublicKey createPublicKey(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    public ConfirmSEQPacket createConfirmSEQPacket() {
        return new ConfirmSEQPacket(encrypt(confirmSEQ));
    }
}