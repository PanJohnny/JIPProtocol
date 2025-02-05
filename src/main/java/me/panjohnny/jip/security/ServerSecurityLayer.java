package me.panjohnny.jip.security;

import me.panjohnny.jip.transport.packet.HandshakePacket;

/**
 * Implementace bezpečnostní vrstvy na straně serveru. Používá se k přijetí handshake od klienta a odeslání RSA klíče zpět. Každý klient má svůj vlastní.
 *
 * @author Jan Štefanča
 * @see SecurityLayer
 * @since 1.0
 */
public final class ServerSecurityLayer extends SecurityLayer {
    private byte[] rsaEncryptedAESKey;

    /**
     * Přijme handshake od klienta a zašifruje AES klíč pomocí RSA.
     *
     * @param handshake handshake data od klienta
     * @throws SecureTransportException pokud se nepodaří zašifrovat AES klíč
     */
    public void acceptClientHandshake(byte[] handshake) throws SecureTransportException {
        var key = generateAESKey();
        this.rsaEncryptedAESKey = encryptRSA(key.getEncoded(), handshake);
    }

    /**
     * Vytvoří handshake paket obsahující zašifrovaný AES klíč.
     *
     * @return handshake paket
     */
    public HandshakePacket createHandshakePacket() {
        return new HandshakePacket(rsaEncryptedAESKey);
    }
}
