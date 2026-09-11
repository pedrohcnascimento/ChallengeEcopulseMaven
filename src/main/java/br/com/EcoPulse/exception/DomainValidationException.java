package br.com.EcoPulse.exception;

public class DomainValidationException extends IllegalArgumentException {
    public DomainValidationException(String message) { super(message); }
}
