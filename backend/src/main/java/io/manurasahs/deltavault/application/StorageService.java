package io.manurasahs.deltavault.application;

import java.io.IOException;

public interface StorageService
{

    void upload(String bucket, String key, byte[] bytes);

    byte[] download(String bucket, String key)
        throws IOException;
}
