package me.panjohnny.jip.security;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Arrays;

public final class ClientSecurityLayer extends SecurityLayer {
    private byte[] confirmSEQ;
    private PublicKey localPublicKey;
    private PrivateKey localPrivateKey;

    public ClientSecurityLayer() throws NoSuchAlgorithmException {
        generateRSAKeypair();
    }

    private void generateRSAKeypair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        this.localPublicKey = pair.getPublic();
        this.localPrivateKey = pair.getPrivate();
    }

    public void acceptServerHandshake(byte[] handshake) throws Exception {
        byte[] decrypted = decryptRSA(handshake); // AES key
        setAESKey(decrypted);
    }

    private byte[] decryptRSA(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, localPrivateKey);
        return cipher.doFinal(data);
    }

    public HandshakePacket createHandshakePacket() {
        return new HandshakePacket(localPublicKey.getEncoded());
    }
}
