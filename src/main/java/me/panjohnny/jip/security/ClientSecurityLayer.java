package me.panjohnny.jip.security;

import javax.crypto.Cipher;

import java.security.*;

/**
 * Security layer implementation on the client. Used to generate RSA keypair, that is used to receive AES key from the server during handshake.
 *
 * @see SecurityLayer
 * @author Jan Štefanča
 */
public final class ClientSecurityLayer extends SecurityLayer {
    private PublicKey localPublicKey;
    private PrivateKey localPrivateKey;

    public ClientSecurityLayer() throws SecureTransportException {
        generateRSAKeypair();
    }

    private void generateRSAKeypair() throws SecureTransportException {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            this.localPublicKey = pair.getPublic();
            this.localPrivateKey = pair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new SecureTransportException("Failed to generate RSA keypair: " + e.getMessage());
        }
    }

    public void acceptServerHandshake(byte[] handshake) throws SecureTransportException {
        byte[] decrypted = decryptRSA(handshake); // AES key
        setAESKey(decrypted);
    }

    private byte[] decryptRSA(byte[] data) throws SecureTransportException {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, localPrivateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new SecureTransportException("Failed to decrypt data with RSA: " + e.getMessage());
        }

    }

    public HandshakePacket createHandshakePacket() {
        return new HandshakePacket(localPublicKey.getEncoded());
    }
}
