package io.manurasahs.deltavault.application.common.exception;

import jakarta.annotation.Nonnull;

public class ObjectNotFoundException extends RuntimeException
{

    public ObjectNotFoundException(@Nonnull String message)
    {
        super(message);
    }
}
