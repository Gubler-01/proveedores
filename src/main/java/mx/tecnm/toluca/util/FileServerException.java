package mx.tecnm.toluca.util;

public class FileServerException extends Exception {
    public FileServerException(String message) {
        super(message);
    }

    public FileServerException(String message, Throwable cause) {
        super(message, cause);
    }
}