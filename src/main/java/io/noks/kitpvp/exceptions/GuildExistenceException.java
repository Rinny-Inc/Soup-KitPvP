package io.noks.kitpvp.exceptions;

public class GuildExistenceException extends Exception {
    public GuildExistenceException(String message) {
        super(message);
    }

    public GuildExistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
