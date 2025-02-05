package me.panjohnny.jip.util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Rozhraní pro zpracování vstupních a výstupních datových proudů.
 * <p>
 * Poskytuje metody pro inicializaci, zpracování a uzavření datových proudů.
 * </p>
 *
 * @author Jan Štefanča
 * @since 1.0
 */
public interface IOProcessor {
    /**
     * Inicializuje výstupní datový proud.
     *
     * @param out výstupní datový proud
     * @throws Exception pokud inicializace selže
     */
    default void init(OutputStream out) throws Exception {
        // Default implementation does nothing
    }

    /**
     * Zpracuje vstupní a výstupní datový proud.
     *
     * @param in  vstupní datový proud
     * @param out výstupní datový proud
     * @throws Exception pokud zpracování selže
     */
    void process(InputStream in, OutputStream out) throws Exception;

    /**
     * Uzavře vstupní datový proud.
     *
     * @param stream vstupní datový proud
     * @throws Exception pokud uzavření selže
     */
    default void close(InputStream stream) throws Exception {
        stream.close();
    }
}