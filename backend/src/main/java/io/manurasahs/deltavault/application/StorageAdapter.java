package io.manurasahs.deltavault.application;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;

public interface StorageAdapter
{

    @Nonnull
    JsonNode downloadObjectFromStorage(@Nonnull String fileKey);

    @Nonnull
    UploadFileResponse uploadObjectToStorage(@Nonnull byte[] objectContent, @Nonnull String fileKey);

    void deleteObjectFromStorage(@Nonnull String fileKey);
}
