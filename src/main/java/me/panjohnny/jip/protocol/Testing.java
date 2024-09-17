package me.panjohnny.jip.protocol;

import me.panjohnny.jip.protocol.packets.Packet;

public class Testing {
    public static void main(String[] args) {
        ClientEncryption clientEncryption = new ClientEncryption();
        clientEncryption.generateKeys();

        var handshake = clientEncryption.createHandshakePacket();

        ServerEncryption serverEncryption = new ServerEncryption(handshake.serialize());
        var confirm = serverEncryption.createConfirmSEQPacket();

        var correct = clientEncryption.finishHandshake(confirm.serialize());
        System.out.println(correct);

        Packet packet = new Packet();
        String hello = "Hello, World!";
        packet.useData(hello.getBytes());

        var enc = serverEncryption.encrypt(packet.serialize());

        System.out.println(new String(packet.serialize()));

        var dec = clientEncryption.decrypt(enc);

        System.out.println(new String(dec));
    }
}
