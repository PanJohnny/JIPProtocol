package me.panjohnny.jip.commons;

import me.panjohnny.jip.transport.Packet;

public abstract class PacketFactory<T extends Packet> {
    public abstract T fabricate();
}
