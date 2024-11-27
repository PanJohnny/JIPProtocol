package me.panjohnny.jip.transport;

public interface TransportMiddleware {
    Packet processWrite(Packet packet);
    Packet processRead(Packet packet);
}
