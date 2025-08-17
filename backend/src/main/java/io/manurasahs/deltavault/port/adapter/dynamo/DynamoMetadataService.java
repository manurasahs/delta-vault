package io.manurasahs.deltavault.port.adapter.dynamo;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import io.manurasahs.deltavault.application.MetadataService;
import io.manurasahs.deltavault.domain.metadata.FileMetadata;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Service
public class DynamoMetadataService implements MetadataService
{

    private final DynamoDbTable<FileMetadata> fileMetadataTable;

    public DynamoMetadataService(DynamoDbEnhancedClient enhancedClient)
    {
        this.fileMetadataTable = enhancedClient.table(
            "FileMetadata",
            TableSchema.fromImmutableClass(FileMetadata.class)
        );
    }

    @Override
    @Nonnull
    public Optional<FileMetadata> getLastFileMetadata(@Nonnull String fileName)
    {
        requireNonNull(fileName);

        return this.fileMetadataTable.query(
                r -> r.queryConditional(
                        QueryConditional.keyEqualTo(Key.builder().partitionValue(fileName).build())
                    )
                    .scanIndexForward(false)
                    .limit(1)
            ).stream()
            .map(Page::items)
            .flatMap(Collection::stream)
            .findFirst();
    }

    @Override
    @Nonnull
    public List<FileMetadata> getLastMetadataDeltas(@Nonnull FileMetadata lastFullFileMetadata)
    {
        requireNonNull(lastFullFileMetadata);

        return this.fileMetadataTable.query(
                r -> r.queryConditional(QueryConditional.sortGreaterThan(
                        Key.builder()
                            .partitionValue(lastFullFileMetadata.fileName())
                            .sortValue(lastFullFileMetadata.version())
                            .build()
                    ))
                    .scanIndexForward(false)
            ).stream()
            .map(Page::items)
            .flatMap(Collection::stream)
            .toList();
    }

    @Override
    @Nonnull
    public Optional<FileMetadata> getLastFullFileMetadata(@Nonnull String fileName)
    {
        requireNonNull(fileName);

        return this.fileMetadataTable.index("GSI1")
            .query(
                r -> r.queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(fileName).build()
                    ))
                    .scanIndexForward(false)
                    .limit(1)
                    .filterExpression(null)
            ).stream()
            .map(Page::items)
            .flatMap(Collection::stream)
            .findFirst();
    }

    @Override
    public void createFileMetadata(@Nonnull FileMetadata fileMetadata)
    {
        requireNonNull(fileMetadata);

        this.fileMetadataTable.putItem(fileMetadata);
    }
}
