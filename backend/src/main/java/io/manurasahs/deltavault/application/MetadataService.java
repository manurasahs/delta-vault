package io.manurasahs.deltavault.application;

import java.util.Optional;

import io.manurasahs.deltavault.domain.metadata.FileMetadata;
import jakarta.annotation.Nonnull;

public interface MetadataService
{

    @Nonnull
    Optional<FileMetadata> getLastFileMetadata(@Nonnull String fileName);

    void createFileMetadata(@Nonnull FileMetadata fileMetadata);
}
