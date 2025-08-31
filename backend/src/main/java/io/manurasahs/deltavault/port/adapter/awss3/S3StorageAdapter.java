package io.manurasahs.deltavault.port.adapter.awss3;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import io.manurasahs.deltavault.application.StorageAdapter;
import io.manurasahs.deltavault.application.UploadFileResponse;
import io.manurasahs.deltavault.application.common.JsonMapper;
import io.manurasahs.deltavault.application.common.exception.S3AccessException;
import io.manurasahs.deltavault.configuration.S3ConfigurationProperties;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3StorageAdapter implements StorageAdapter
{

    private final S3Client s3Client;

    private final JsonMapper jsonMapper;

    private final S3ConfigurationProperties s3ConfigurationProperties;

    public S3StorageAdapter(
        S3Client s3Client,
        JsonMapper jsonMapper,
        S3ConfigurationProperties s3ConfigurationProperties
    )
    {
        this.s3Client = s3Client;
        this.jsonMapper = jsonMapper;
        this.s3ConfigurationProperties = s3ConfigurationProperties;
    }

    @Nonnull
    @Override
    public JsonNode downloadObjectFromStorage(@Nonnull String fileKey)
    {
        var request = GetObjectRequest.builder()
            .bucket(this.s3ConfigurationProperties.bucketName())
            .key(fileKey)
            .build();
        try (var inputStream = this.s3Client.getObject(request))
        {
            return this.jsonMapper.readTree(inputStream.readAllBytes());
        }
        catch (IOException e)
        {
            throw new S3AccessException(STR."Failed to download object \{fileKey} from S3.", e);
        }
    }

    @Nonnull
    @Override
    public UploadFileResponse uploadObjectToStorage(@Nonnull byte[] objectContent, @Nonnull String fileKey)
    {
        var response = this.s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(this.s3ConfigurationProperties.bucketName())
                .key(fileKey)
                .contentType("application/json")
                .checksumAlgorithm(ChecksumAlgorithm.SHA256)
                .build(),
            RequestBody.fromBytes(objectContent)
        );
        return new UploadFileResponse(response.checksumSHA256(), fileKey);
    }

    @Override
    public void deleteObjectFromStorage(@Nonnull String fileKey)
    {
        this.s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(this.s3ConfigurationProperties.bucketName())
                .key(fileKey)
                .build()
        );
    }
}
