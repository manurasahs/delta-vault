package io.manurasahs.deltavault.application.saga.step;

import io.manurasahs.deltavault.domain.saga.model.Saga;
import io.manurasahs.deltavault.domain.saga.model.SagaStepName;
import jakarta.annotation.Nonnull;

public sealed interface SagaStep permits CreateMetadataStep, UploadFileStep, FinalizeStep
{

    @Nonnull
    SagaStepName stepName();

    void execute(@Nonnull Saga saga);
//        throws RetryableException, FatalException;

    void compensate(@Nonnull Saga saga);
//        throws RetryableException, FatalException;
}
