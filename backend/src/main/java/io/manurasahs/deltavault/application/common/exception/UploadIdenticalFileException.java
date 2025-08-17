package io.manurasahs.deltavault.application.common.exception;

public class UploadIdenticalFileException extends RuntimeException
{

    public UploadIdenticalFileException()
    {
        super("An attempt to upload a file identical to the previous version.");
    }
}
