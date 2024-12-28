package me.panjohnny.jip.server;

import me.panjohnny.jip.commons.RequestPacket;
import me.panjohnny.jip.commons.ResponsePacket;

public interface RequestHandler {
    void handle(RequestPacket requestPacket, ResponsePacket responsePacket);
}
