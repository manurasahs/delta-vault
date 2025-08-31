package io.manurasahs.deltavault.application.saga.step;

import io.manurasahs.deltavault.domain.metadata.MetadataRepository;
import io.manurasahs.deltavault.domain.metadata.model.MetadataStatus;
import io.manurasahs.deltavault.domain.saga.model.Saga;
import io.manurasahs.deltavault.domain.saga.model.SagaStepName;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public final class FinalizeStep implements SagaStep
{

    private final MetadataRepository metadataRepository;

    public FinalizeStep(MetadataRepository metadataRepository)
    {
        this.metadataRepository = metadataRepository;
    }

    @Nonnull
    @Override
    public SagaStepName stepName()
    {
        return SagaStepName.FINALIZE;
    }

    @Override
    public void execute(@Nonnull Saga saga)
    {
        this.metadataRepository.updateFileMetadata(saga.metadata().withStatus(MetadataStatus.SUCCESS));
    }

    @Override
    public void compensate(@Nonnull Saga saga)
    {
        this.metadataRepository.updateFileMetadata(saga.metadata().withStatus(MetadataStatus.FAILED));
    }
}
