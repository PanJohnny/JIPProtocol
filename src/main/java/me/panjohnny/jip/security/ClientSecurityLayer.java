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
        byte[] confirmSEQ = new byte[2];
        System.arraycopy(handshake, 0, confirmSEQ, 0, 2);
        byte[] pk = new byte[handshake.length - 2];
        System.arraycopy(handshake, 2, pk, 0, pk.length);

        if (!Arrays.equals(this.confirmSEQ, decryptRSA(confirmSEQ))) {
            throw new HandshakeFailedException("Server confirmSEQ does not match client confirmSEQ");
        }

        byte[] decrypted = decryptRSA(pk); // AES key
        setAESKey(decrypted);
    }

    private byte[] decryptRSA(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, localPrivateKey);
        System.out.println("Arrays.toString(data) = " + Arrays.toString(data));
        return cipher.doFinal(data);
    }

    public HandshakePacket createHandshakePacket() {
        this.confirmSEQ = createConfirmSEQ();
        System.out.println("Arrays.toString(confirmSEQ) = " + Arrays.toString(confirmSEQ));
        return new HandshakePacket(confirmSEQ, localPublicKey.getEncoded());
    }
}
