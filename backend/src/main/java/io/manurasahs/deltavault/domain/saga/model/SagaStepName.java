package io.manurasahs.deltavault.domain.saga.model;

public enum SagaStepName
{
    CREATE_METADATA_IN_DDB(0),
    UPLOAD_FILE_TO_S3(1),
    FINALIZE(2);

    private final int executionOrder;

    SagaStepName(int executionOrder)
    {
        this.executionOrder = executionOrder;
    }

    public int executionOrder()
    {
        return this.executionOrder;
    }
}
