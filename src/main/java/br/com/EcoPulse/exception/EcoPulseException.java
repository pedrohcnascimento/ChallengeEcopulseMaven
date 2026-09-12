package br.com.EcoPulse.exception;

public class EcoPulseException extends RuntimeException {
    public EcoPulseException(String message) { super(message); }
    public EcoPulseException(String message, Throwable cause) { super(message, cause); }
}
