package io.manurasahs.deltavault.application;

import java.util.List;
import java.util.Optional;

import io.manurasahs.deltavault.domain.metadata.FileMetadata;
import jakarta.annotation.Nonnull;

public interface MetadataService
{

    @Nonnull
    Optional<FileMetadata> getLastFileMetadata(@Nonnull String fileName);

    @Nonnull
    List<FileMetadata> getLastMetadataDeltas(@Nonnull FileMetadata lastFullFileMetadata);

    @Nonnull
     Optional<FileMetadata> getLastFullFileMetadata(@Nonnull String fileName);

    void createFileMetadata(@Nonnull FileMetadata fileMetadata);
}
