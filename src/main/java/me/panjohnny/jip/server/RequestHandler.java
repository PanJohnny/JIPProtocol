package me.panjohnny.jip.server;

import me.panjohnny.jip.commons.Request;
import me.panjohnny.jip.commons.Response;

public interface RequestHandler {
    void handle(Request request, Response response);
}
