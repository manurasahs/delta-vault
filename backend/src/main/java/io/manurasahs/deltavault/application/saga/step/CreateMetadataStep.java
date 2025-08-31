package io.manurasahs.deltavault.application.saga.step;

import io.manurasahs.deltavault.domain.metadata.MetadataRepository;
import io.manurasahs.deltavault.domain.saga.model.Saga;
import io.manurasahs.deltavault.domain.saga.model.SagaStepName;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public final class CreateMetadataStep implements SagaStep
{

    private final MetadataRepository metadataRepository;

    public CreateMetadataStep(MetadataRepository metadataRepository)
    {
        this.metadataRepository = metadataRepository;
    }

    @Nonnull
    @Override
    public SagaStepName stepName()
    {
        return SagaStepName.CREATE_METADATA_IN_DDB;
    }

    @Override
    public void execute(@Nonnull Saga saga)
    {
        this.metadataRepository.createFileMetadata(saga.metadata());
    }

    @Override
    public void compensate(@Nonnull Saga saga)
    {
        this.metadataRepository.deleteFileMetadata(saga.metadata());
    }
}
