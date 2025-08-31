package io.manurasahs.deltavault.domain.metadata.model;

import java.time.Instant;

import jakarta.annotation.Nonnull;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbImmutable(builder = FileMetadata.Builder.class)
public record FileMetadata(
    @DynamoDbSecondaryPartitionKey(indexNames = "GSI1") @DynamoDbPartitionKey @Nonnull String fileName,
    @DynamoDbSortKey int version,
    @Nonnull Instant createdAt,
    @Nonnull MetadataType type,
    @Nonnull String checksum,
    int fileSize,
    int deltaSize,
    @Nonnull MetadataStatus status
)
{

    @DynamoDbSecondarySortKey(indexNames = "GSI1")
    public String getTypeRankVersion()
    {
        var versionPadded = String.format("%010d", version);
        return STR."\{this.type.rank()}#\{versionPadded}";
    }

    @Nonnull
    public static FileMetadata metadataForNewFile(
        @Nonnull String fileName,
        @Nonnull Instant createdAt,
        @Nonnull String checksum,
        int fileSize
    )
    {
        return new FileMetadata(
            fileName,
            0,
            createdAt,
            MetadataType.SNAPSHOT,
            checksum,
            fileSize,
            fileSize,
            MetadataStatus.IN_PROGRESS
        );
    }

    @DynamoDbIgnore
    @Nonnull
    public String fileKey()
    {
        return STR."\{fileName}/\{version}.json";
    }

    @DynamoDbIgnore
    @Nonnull
    public FileMetadata withStatus(@Nonnull MetadataStatus status)
    {
        return new FileMetadata(
            this.fileName,
            this.version,
            this.createdAt,
            this.type,
            this.checksum,
            this.fileSize,
            this.deltaSize,
            status
        );
    }

    public FileMetadata(@Nonnull Builder builder)
    {
        this(
            builder.fileName,
            builder.version,
            builder.createdAt,
            builder.type,
            builder.checksum,
            builder.fileSize,
            builder.deltaSize,
            builder.status
        );
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

        private int fileSize;

        private int deltaSize;

        private MetadataStatus status;

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
        public Builder fileSize(int fileSize)
        {
            this.fileSize = fileSize;
            return this;
        }

        @Nonnull
        public Builder deltaSize(int deltaSize)
        {
            this.deltaSize = deltaSize;
            return this;
        }

        @Nonnull
        public Builder status(@Nonnull MetadataStatus status)
        {
            this.status = status;
            return this;
        }

        @Nonnull
        public Builder typeRankVersion(String ignored)
        {
            // no-op: derived attribute; kept for schema compatibility when reading
            return this;
        }

        @Nonnull
        public FileMetadata build()
        {
            return new FileMetadata(this);
        }
    }
}
