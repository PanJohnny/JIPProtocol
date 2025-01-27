package me.panjohnny.jip.util;

import java.io.InputStream;
import java.io.OutputStream;

public interface IOProcessor {
    default void init(OutputStream out) throws Exception {

    }

    void process(InputStream in, OutputStream out) throws Exception;

    default void close(InputStream stream) throws Exception {
        stream.close();
    }
}
