package me.panjohnny.jip.security;

import me.panjohnny.jip.transport.packet.HandshakePacket;

import javax.crypto.Cipher;
import java.security.*;

/**
 * Implementace bezpečnostní vrstvy na straně klienta. Používá se k vygenerování RSA klíčového páru, který se používá k přijetí AES klíče od serveru během handshake.
 *
 * @author Jan Štefanča
 * @see SecurityLayer
 * @since 1.0
 */
public final class ClientSecurityLayer extends SecurityLayer {
    private PublicKey localPublicKey;
    private PrivateKey localPrivateKey;

    /**
     * Vytvoří novou instanci ClientSecurityLayer a vygeneruje RSA klíčový pár.
     *
     * @throws SecureTransportException pokud se nepodaří vygenerovat RSA klíčový pár
     */
    public ClientSecurityLayer() throws SecureTransportException {
        generateRSAKeypair();
    }

    /**
     * Vygeneruje RSA klíčový pár.
     *
     * @throws SecureTransportException pokud se nepodaří vygenerovat RSA klíčový pár
     */
    private void generateRSAKeypair() throws SecureTransportException {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            this.localPublicKey = pair.getPublic();
            this.localPrivateKey = pair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new SecureTransportException("Nepodařilo se vygenerovat RSA klíčový pár: " + e.getMessage());
        }
    }

    /**
     * Přijme handshake od serveru a dešifruje AES klíč.
     *
     * @param handshake handshake data
     * @throws SecureTransportException pokud se nepodaří dešifrovat data
     */
    public void acceptServerHandshake(byte[] handshake) throws SecureTransportException {
        byte[] decrypted = decryptRSA(handshake); // AES klíč
        setAESKey(decrypted);
    }

    /**
     * Dešifruje data pomocí RSA.
     *
     * @param data data k dešifrování
     * @return dešifrovaná data
     * @throws SecureTransportException pokud se nepodaří dešifrovat data
     */
    private byte[] decryptRSA(byte[] data) throws SecureTransportException {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, localPrivateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new SecureTransportException("Nepodařilo se dešifrovat data pomocí RSA: " + e.getMessage());
        }
    }

    /**
     * Vytvoří handshake paket obsahující veřejný klíč.
     *
     * @return handshake paket
     */
    public HandshakePacket createHandshakePacket() {
        return new HandshakePacket(localPublicKey.getEncoded());
    }
}