package me.panjohnny.jip.protocol;

import me.panjohnny.jip.protocol.packets.HandshakePacket;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class ClientEncryption {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private byte[] confirmSEQ;

    public ClientEncryption() {
        // Constructor
    }

    public void generateKeys() {
        try {
            // Create a KeyPairGenerator for RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048); // Key size

            // Generate the key pair
            KeyPair pair = keyGen.generateKeyPair();
            this.publicKey = pair.getPublic();
            this.privateKey = pair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public byte[] createConfirmSEQ() {
        SecureRandom random = new SecureRandom();
        byte[] confirmSEQ = new byte[2];
        random.nextBytes(confirmSEQ);
        this.confirmSEQ = confirmSEQ;
        return confirmSEQ;
    }

    public HandshakePacket createHandshakePacket() {
        return new HandshakePacket(createConfirmSEQ(), publicKey.getEncoded());
    }

    public byte[] decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Decryption error", e);
        }
    }

    public boolean finishHandshake(byte[] confirmSEQEncrypted) {
        byte[] confirmSEQ = decrypt(confirmSEQEncrypted);
        return confirmSEQ[0] == this.confirmSEQ[0] && confirmSEQ[1] == this.confirmSEQ[1];
    }
}