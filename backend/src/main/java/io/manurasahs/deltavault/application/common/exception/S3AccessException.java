package io.manurasahs.deltavault.application.common.exception;

import jakarta.annotation.Nonnull;

public class S3AccessException extends RuntimeException
{

    public S3AccessException(@Nonnull String message, @Nonnull Throwable cause)
    {
        super(message, cause);
    }
}
