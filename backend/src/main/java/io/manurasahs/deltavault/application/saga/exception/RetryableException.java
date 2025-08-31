package io.manurasahs.deltavault.application.saga.exception;

public class RetryableException extends Exception
{

    public RetryableException(String message)
    {
        super(message);
    }
}
