package me.panjohnny.jip.security;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public final class ServerSecurityLayer extends SecurityLayer {
    private byte[] confirmSEQ;
    private byte[] rsaEncryptedAESKey;

    public void acceptClientHandshake(byte[] handshake) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] confirmSEQ = new byte[2];
        System.arraycopy(handshake, 0, confirmSEQ, 0, 2);
        byte[] pk = new byte[handshake.length - 2];
        System.arraycopy(handshake, 2, pk, 0, pk.length);
        this.confirmSEQ = encryptRSA(confirmSEQ, pk);
        var key = generateAESKey();
        this.rsaEncryptedAESKey = encryptRSA(key.getEncoded(), pk);
    }

    public HandshakePacket createHandshakePacket() throws Exception {
        return new HandshakePacket(confirmSEQ, rsaEncryptedAESKey);
    }
}
