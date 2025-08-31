package io.manurasahs.deltavault.application.saga.exception;

public class FatalException extends Exception
{

    public FatalException(String message)
    {
        super(message);
    }
}
