package me.panjohnny.jip.transport;

import me.panjohnny.jip.util.IOProcessor;

public interface TransportMiddleware {
    Packet processWrite(Packet packet);
    Packet processRead(Packet packet);
    IOProcessor processIO(Packet packet) throws Exception;
}