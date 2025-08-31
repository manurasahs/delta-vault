package io.manurasahs.deltavault.domain.saga.model;

import io.manurasahs.deltavault.domain.metadata.model.FileMetadata;
import jakarta.annotation.Nonnull;

public record Saga(
//    @Nonnull String id,
//    @Nonnull SagaStatus status,
//    @Nonnull SagaStepName step,
//    int attempt,
//    @Nullable String error,
    @Nonnull FileMetadata metadata,
    @Nonnull byte[] file
)
{
}
