package io.manurasahs.deltavault.domain.metadata;

public enum MetadataType
{
    DELTA(1),
    FULL_FILE(2);

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
