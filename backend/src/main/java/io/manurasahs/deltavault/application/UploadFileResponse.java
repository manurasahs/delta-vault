package io.manurasahs.deltavault.application;

import jakarta.annotation.Nonnull;

public record UploadFileResponse(
    @Nonnull String checksum,
    @Nonnull String fileKey
)
{
}
