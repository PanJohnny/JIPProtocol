package me.panjohnny.jip.security;

/**
 * Výjimka pro chyby v bezpečném přenosu.
 * <p>
 * Tato třída představuje výjimku, která je vyvolána při chybách v bezpečném přenosu dat.
 * </p>
 *
 * @author Jan Štefanča
 * @see Exception
 * @since 1.0
 */
public class SecureTransportException extends Exception {
    /**
     * Vytvoří novou výjimku s danou zprávou.
     *
     * @param message zpráva popisující chybu
     */
    public SecureTransportException(String message) {
        super(message);
    }

    /**
     * Vytvoří novou výjimku s danou zprávou a příčinou.
     *
     * @param message zpráva popisující chybu
     * @param cause   původní výjimka, která způsobila tuto výjimku
     */
    public SecureTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}