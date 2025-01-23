package me.panjohnny.jip.server;

import me.panjohnny.jip.commons.Response;
import me.panjohnny.jip.transport.packet.RequestPacket;

import java.util.Map;

public interface RequestHandler {
    default void handle(RequestPacket req, Response res) {
        handle(req, res, null);
    };
    void handle(RequestPacket req, Response res, Map<String, String> params);
}
