package me.panjohnny.jip.security;

/**
 * Thrown when there is an error while handshaking or when encryption/decryption of packets fails.
 *
 * @author Jan Štefanča
 */
public class SecureTransportException extends Exception {
    public SecureTransportException(String message) {
        super(message);
    }

    public SecureTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
