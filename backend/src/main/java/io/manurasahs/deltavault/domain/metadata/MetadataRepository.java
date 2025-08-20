package io.manurasahs.deltavault.domain.metadata;

import java.util.List;
import java.util.Optional;

import io.manurasahs.deltavault.domain.metadata.model.FileMetadata;
import jakarta.annotation.Nonnull;

public interface MetadataRepository
{

    @Nonnull
    Optional<FileMetadata> getLastFileMetadata(@Nonnull String fileName);

    @Nonnull
    List<FileMetadata> getLastMetadataDeltas(@Nonnull FileMetadata lastFullFileMetadata);

    @Nonnull
     Optional<FileMetadata> getLastFullFileMetadata(@Nonnull String fileName);

    void createFileMetadata(@Nonnull FileMetadata fileMetadata);
}
