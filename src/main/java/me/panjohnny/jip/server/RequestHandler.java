package me.panjohnny.jip.server;

import me.panjohnny.jip.commons.Response;
import me.panjohnny.jip.transport.packet.RequestPacket;

import java.util.Map;

/**
 * Rozhraní pro zpracování požadavků.
 *
 * @author Jan Štefanča
 * @see RequestPacket
 * @see Response
 * @since 1.0
 */
public interface RequestHandler {
    /**
     * Zpracuje požadavek bez parametrů.
     *
     * @param req požadavek
     * @param res odpověď
     */
    default void handle(RequestPacket req, Response res) {
        handle(req, res, null);
    }

    /**
     * Zpracuje požadavek s parametry.
     *
     * @param req    požadavek
     * @param res    odpověď
     * @param params parametry požadavku
     */
    void handle(RequestPacket req, Response res, Map<String, String> params);
}