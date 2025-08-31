package io.manurasahs.deltavault.application.saga.step;

import java.util.Base64;

import io.manurasahs.deltavault.application.StorageAdapter;
import io.manurasahs.deltavault.domain.saga.model.Saga;
import io.manurasahs.deltavault.domain.saga.model.SagaStepName;
import jakarta.annotation.Nonnull;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public final class UploadFileStep implements SagaStep
{

    private final StorageAdapter storageAdapter;

    public UploadFileStep(StorageAdapter storageAdapter)
    {
        this.storageAdapter = storageAdapter;
    }

    @Nonnull
    @Override
    public SagaStepName stepName()
    {
        return SagaStepName.UPLOAD_FILE_TO_S3;
    }

    @Override
    public void execute(@Nonnull Saga saga)
    {
        var checksum = Base64.getEncoder().encodeToString(DigestUtils.sha256(saga.file()));
        var response = this.storageAdapter.uploadObjectToStorage(saga.file(), saga.metadata().fileKey());
        if (!checksum.equals(response.checksum()))
        {
            throw new IllegalStateException("File corrupted.");
        }
    }

    @Override
    public void compensate(@Nonnull Saga saga)
    {
        this.storageAdapter.deleteObjectFromStorage(saga.metadata().fileKey());
    }
}
