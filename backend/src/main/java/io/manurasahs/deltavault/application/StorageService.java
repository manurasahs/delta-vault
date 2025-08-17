package io.manurasahs.deltavault.application;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;

public interface StorageService
{

    void upload(@Nonnull String fileName, @Nonnull Map<String, Object> fileContent);

    @Nonnull
    JsonNode download(@Nonnull String key);
}
