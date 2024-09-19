package me.panjohnny.jip.security;

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
