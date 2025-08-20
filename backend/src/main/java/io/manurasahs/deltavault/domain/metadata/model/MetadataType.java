package io.manurasahs.deltavault.domain.metadata.model;

public enum MetadataType
{
    DELTA(1),
    SNAPSHOT(2);

    private final int rank;

    MetadataType(int rank)
    {
        this.rank = rank;
    }

    public int rank()
    {
        return this.rank;
    }
}
