package io.manurasahs.deltavault.port.adapter.awss3;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

import io.manurasahs.deltavault.application.MetadataService;
import io.manurasahs.deltavault.application.StorageService;
import io.manurasahs.deltavault.domain.metadata.FileMetadata;
import io.manurasahs.deltavault.domain.metadata.MetadataType;
import jakarta.annotation.Nonnull;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3StorageService implements StorageService
{

    private final S3Client s3Client;

    private final MetadataService metadataService;

    public S3StorageService(S3Client s3Client, MetadataService metadataService)
    {
        this.s3Client = s3Client;
        this.metadataService = metadataService;
    }

    @Override
    public void upload(@Nonnull String bucket, @Nonnull String key, @Nonnull byte[] bytes)
    {

        var metadata = new FileMetadata(
            key,
            2,
            Instant.now(),
            MetadataType.FULL_FILE,
            DigestUtils.sha256Hex(bytes),
            bytes.length
        );
        this.metadataService.createFileMetadata(metadata);
        this.s3Client.putObject(
            PutObjectRequest.builder().bucket(bucket).key(key).build(),
            RequestBody.fromBytes(bytes)
        );
    }

    public void uploadFile(String bucket, String key, Path file)
    {
        this.s3Client.putObject(
            PutObjectRequest.builder().bucket(bucket).key(key).build(),
            RequestBody.fromFile(file)
        );
    }

    @Override
    public byte[] download(String bucket, String key)
        throws IOException
    {
        var metadata = this.metadataService.getLastFileMetadata(key);
        System.out.println(metadata);
        return this.s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build()).readAllBytes();
    }

    public void delete(String bucket, String key)
    {
        this.s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }
}
