package me.panjohnny.jip.security;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public final class ServerSecurityLayer extends SecurityLayer {
    private byte[] rsaEncryptedAESKey;

    public void acceptClientHandshake(byte[] handshake) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        var key = generateAESKey();
        this.rsaEncryptedAESKey = encryptRSA(key.getEncoded(), handshake);
    }

    public HandshakePacket createHandshakePacket() throws Exception {
        return new HandshakePacket(rsaEncryptedAESKey);
    }
}
