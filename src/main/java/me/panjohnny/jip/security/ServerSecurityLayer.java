package me.panjohnny.jip.security;

import me.panjohnny.jip.transport.packet.HandshakePacket;

/**
 * Server security layer used to accept client handshake and send back RSA key. Every client has its own.
 *
 * @see SecurityLayer
 * @author Jan Štefanča
 */
public final class ServerSecurityLayer extends SecurityLayer {
    private byte[] rsaEncryptedAESKey;

    public void acceptClientHandshake(byte[] handshake) throws SecureTransportException {
        var key = generateAESKey();
        this.rsaEncryptedAESKey = encryptRSA(key.getEncoded(), handshake);
    }

    public HandshakePacket createHandshakePacket() throws Exception {
        return new HandshakePacket(rsaEncryptedAESKey);
    }
}
