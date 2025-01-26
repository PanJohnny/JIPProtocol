package me.panjohnny.jip.transport;

import me.panjohnny.jip.util.IOProcessor;

import java.io.IOException;

public interface TransportMiddleware {
    Packet processWrite(Packet packet);
    Packet processRead(Packet packet) throws IOException;
    IOProcessor processIO(Packet packet) throws Exception;
}