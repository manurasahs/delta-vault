package io.manurasahs.deltavault.port.adapter.awss3;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import io.manurasahs.deltavault.application.DeltaService;
import io.manurasahs.deltavault.application.MetadataService;
import io.manurasahs.deltavault.application.StorageService;
import io.manurasahs.deltavault.application.common.JsonMapper;
import io.manurasahs.deltavault.application.common.exception.UploadIdenticalFileException;
import io.manurasahs.deltavault.domain.metadata.FileMetadata;
import io.manurasahs.deltavault.domain.metadata.MetadataType;
import jakarta.annotation.Nonnull;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3StorageService implements StorageService
{

    private final S3Client s3Client;

    private final MetadataService metadataService;

    private final DeltaService deltaService;

    private final JsonMapper jsonMapper;

    private final String bucketName;

    public S3StorageService(
        S3Client s3Client,
        MetadataService metadataService,
        DeltaService deltaService,
        JsonMapper jsonMapper,
        @Value("${deltavault.s3.bucketName}") String bucketName
    )
    {
        this.s3Client = s3Client;
        this.metadataService = metadataService;
        this.deltaService = deltaService;
        this.jsonMapper = jsonMapper;
        this.bucketName = bucketName;
    }

    @Override
    public void upload(@Nonnull String fileName, @Nonnull Map<String, Object> fileContent)
    {
        var timestamp = Instant.now();
        var bytes = this.jsonMapper.serializeToBytes(fileContent);
        this.metadataService.getLastFileMetadata(fileName)
            .ifPresentOrElse(
                metadata -> updateExistedFile(fileName, timestamp, metadata, bytes),
                () -> saveFileWithFileMetadata(
                    bytes,
                    FileMetadata.metadataForNewFile(fileName, timestamp, DigestUtils.sha256Hex(bytes), bytes.length)
                )
            );
    }

    private void updateExistedFile(
        @Nonnull String fileName,
        @Nonnull Instant timestamp,
        @Nonnull FileMetadata previuousFileMetadata,
        @Nonnull byte[] fileContent
    )
    {
        var checksum = DigestUtils.sha256Hex(fileContent);
        if (previuousFileMetadata.checksum().equals(checksum))
        {
            throw new UploadIdenticalFileException();
        }
        var newVersion = previuousFileMetadata.version() + 1;
        if (newVersion % 5 == 0)
        {
            saveFileWithFileMetadata(
                fileContent,
                new FileMetadata(fileName, newVersion, timestamp, MetadataType.FULL_FILE, checksum, fileContent.length)
            );
        } else
        {
            var diff = this.deltaService.computeDuff(
                this.jsonMapper.readTree(downloadObjectFromS3(previuousFileMetadata.fileKey())),
                this.jsonMapper.readTree(fileContent)
            );
            var diffBytes = this.jsonMapper.serializeToBytes(diff);
            saveFileWithFileMetadata(
                diffBytes,
                new FileMetadata(fileName, newVersion, timestamp, MetadataType.DELTA, checksum, diffBytes.length)
            );
        }
    }

    private void saveFileWithFileMetadata(@Nonnull byte[] fileContent, @Nonnull FileMetadata metadata)
    {
        // todo think how to achieve data consistency
        this.metadataService.createFileMetadata(metadata);
        this.s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(this.bucketName)
                .key(metadata.fileKey())
                .build(),
            RequestBody.fromBytes(fileContent)
        );
    }

    @Nonnull
    @Override
    public JsonNode download(@Nonnull String fileName)
    {
        // note: here lastFullFileMetadata is not hydrated with all fields, contains only keys because of "ProjectionType": "KEYS_ONLY"
        var lastFullFileMetadata = this.metadataService.getLastFullFileMetadata(fileName)
            .orElseThrow(() -> new IllegalStateException("Not exist FULL_FILE version in metadata table."));
        var metadataDeltas = this.metadataService.getLastMetadataDeltas(lastFullFileMetadata).stream()
            .map(FileMetadata::fileKey)
            .map(this::downloadObjectFromS3)
            .toList();
        return this.deltaService.restoreFile(
            this.jsonMapper.readTree(downloadObjectFromS3(lastFullFileMetadata.fileKey())),
            metadataDeltas
        );
    }

    @Nonnull
    private byte[] downloadObjectFromS3(@Nonnull String fileKey)
    {
        try
        {
            return this.s3Client.getObject(GetObjectRequest.builder().bucket(this.bucketName).key(fileKey).build())
                .readAllBytes();
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
