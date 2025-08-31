package io.manurasahs.deltavault.application;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import io.manurasahs.deltavault.application.common.JsonMapper;
import io.manurasahs.deltavault.application.common.exception.ObjectNotFoundException;
import io.manurasahs.deltavault.application.common.exception.UploadIdenticalFileException;
import io.manurasahs.deltavault.application.saga.SagaOrchestrator;
import io.manurasahs.deltavault.domain.metadata.MetadataRepository;
import io.manurasahs.deltavault.domain.metadata.model.FileMetadata;
import io.manurasahs.deltavault.domain.metadata.model.MetadataStatus;
import io.manurasahs.deltavault.domain.metadata.model.MetadataType;
import jakarta.annotation.Nonnull;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3StorageService
{

    private final MetadataRepository metadataRepository;

    private final StorageAdapter storageAdapter;

    private final JsonMapper jsonMapper;

    private final SagaOrchestrator sagaOrchestrator;

    private final int snapshotsFrequency;

    public S3StorageService(
        MetadataRepository metadataRepository,
        StorageAdapter storageAdapter,
        JsonMapper jsonMapper,
        SagaOrchestrator sagaOrchestrator,
        @Value("${deltavault.snapshots-frequency}") int snapshotsFrequency
    )
    {
        this.metadataRepository = metadataRepository;
        this.storageAdapter = storageAdapter;
        this.jsonMapper = jsonMapper;
        this.sagaOrchestrator = sagaOrchestrator;
        if (snapshotsFrequency <= 0)
        {
            throw new IllegalStateException("Invalid configuration: snapshot frequency must be greater than 0.");
        }
        this.snapshotsFrequency = snapshotsFrequency;
    }

    public void upload(@Nonnull String fileName, @Nonnull Map<String, Object> fileContent)
    {
        var timestamp = Instant.now();
        var bytes = this.jsonMapper.serializeToBytes(fileContent);
        this.metadataRepository.getLastFileMetadata(fileName)
            .ifPresentOrElse(
                metadata -> updateExistedFile(fileName, timestamp, metadata, bytes),
                () -> saveFileWithFileMetadata(
                    bytes,
                    FileMetadata.metadataForNewFile(fileName, timestamp, DigestUtils.sha256Hex(bytes), bytes.length)
                )
            );
    }

    @Nonnull
    public JsonNode download(@Nonnull String fileName)
    {
        // note: here lastFullFileMetadata is not hydrated with all fields, contains only keys because of "ProjectionType": "KEYS_ONLY"
        var lastFullFileMetadata = this.metadataRepository.getLastFullFileMetadata(fileName)
            .orElseThrow(() -> new ObjectNotFoundException(STR."File \{fileName} not found."));
        var metadataDeltas = this.metadataRepository.getLastMetadataDeltas(lastFullFileMetadata).stream()
            .map(FileMetadata::fileKey)
            .map(this.storageAdapter::downloadObjectFromStorage)
            .toList();
        return DeltaReader.restoreFile(
            this.storageAdapter.downloadObjectFromStorage(lastFullFileMetadata.fileKey()),
            metadataDeltas
        );
    }

    private void updateExistedFile(
        @Nonnull String fileName,
        @Nonnull Instant timestamp,
        @Nonnull FileMetadata previousFileMetadata,
        @Nonnull byte[] fileContent
    )
    {
        var checksum = DigestUtils.sha256Hex(fileContent);
        if (previousFileMetadata.checksum().equals(checksum))
        {
            throw new UploadIdenticalFileException();
        }
        var newVersion = previousFileMetadata.version() + 1;
        if (newVersion % this.snapshotsFrequency == 0)
        {
            saveFileWithFileMetadata(
                fileContent,
                new FileMetadata(
                    fileName,
                    newVersion,
                    timestamp,
                    MetadataType.SNAPSHOT,
                    checksum,
                    fileContent.length,
                    fileContent.length,
                    MetadataStatus.IN_PROGRESS
                )
            );
        } else
        {
            var diff = DeltaReader.computeDiff(
                this.storageAdapter.downloadObjectFromStorage(previousFileMetadata.fileKey()),
                this.jsonMapper.readTree(fileContent)
            );
            var diffBytes = this.jsonMapper.serializeToBytes(diff);
            saveFileWithFileMetadata(
                diffBytes,
                new FileMetadata(
                    fileName,
                    newVersion,
                    timestamp,
                    MetadataType.DELTA,
                    checksum,
                    fileContent.length,
                    diffBytes.length,
                    MetadataStatus.IN_PROGRESS
                )
            );
        }
    }

    private void saveFileWithFileMetadata(@Nonnull byte[] fileContent, @Nonnull FileMetadata metadata)
    {
        this.sagaOrchestrator.startSaga(metadata, fileContent);

        var checksum = Base64.getEncoder().encodeToString(DigestUtils.sha256(fileContent));
        var response = this.storageAdapter.uploadObjectToStorage(fileContent, metadata.fileKey());
        if (!checksum.equals(response.checksum()))
        {
            throw new IllegalStateException("File corrupted.");
        }
        this.metadataRepository.createFileMetadata(metadata);
    }
}
