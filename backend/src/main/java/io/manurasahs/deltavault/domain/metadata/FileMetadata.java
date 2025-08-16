package io.manurasahs.deltavault.domain.metadata;

import static java.util.Objects.requireNonNull;

import java.time.Instant;

import jakarta.annotation.Nonnull;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbImmutable(builder = FileMetadata.Builder.class)
public record FileMetadata(
    @DynamoDbPartitionKey @Nonnull String fileName,
    @DynamoDbSortKey int version,
    @Nonnull Instant createdAt,
    @Nonnull MetadataType type,
    @Nonnull String checksum,
    int size
)
{

    public FileMetadata
    {
        requireNonNull(fileName);
        requireNonNull(createdAt);
        requireNonNull(type);
        requireNonNull(checksum);
    }

    public FileMetadata(@Nonnull Builder builder)
    {
        this(builder.fileName, builder.version, builder.createdAt, builder.type, builder.checksum, builder.size);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String fileName;

        private int version;

        private Instant createdAt;

        private MetadataType type;

        private String checksum;

        private int size;

        private Builder() {}

        @Nonnull
        public Builder fileName(@Nonnull String fileName)
        {
            this.fileName = fileName;
            return this;
        }

        @Nonnull
        public Builder version(int version)
        {
            this.version = version;
            return this;
        }

        @Nonnull
        public Builder createdAt(@Nonnull Instant createdAt)
        {
            this.createdAt = createdAt;
            return this;
        }

        @Nonnull
        public Builder type(@Nonnull MetadataType type)
        {
            this.type = type;
            return this;
        }

        @Nonnull
        public Builder checksum(@Nonnull String checksum)
        {
            this.checksum = checksum;
            return this;
        }

        @Nonnull
        public Builder size(int size)
        {
            this.size = size;
            return this;
        }

        @Nonnull
        public FileMetadata build()
        {
            return new FileMetadata(this);
        }
    }
}
