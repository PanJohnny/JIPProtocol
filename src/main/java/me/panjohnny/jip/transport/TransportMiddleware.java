package me.panjohnny.jip.transport;

public interface TransportMiddleware {
    Packet proccessWrite(Packet packet);
    Packet proccessRead(Packet packet);
}
