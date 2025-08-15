package io.manurasahs.deltavault.port.adapter.awss3;

import java.io.IOException;
import java.nio.file.Path;

import io.manurasahs.deltavault.application.StorageService;
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

    public S3StorageService(S3Client s3Client)
    {
        this.s3Client = s3Client;
    }

    @Override
    public void upload(String bucket, String key, byte[] bytes)
    {
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
        return this.s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build()).readAllBytes();
    }

    public void delete(String bucket, String key)
    {
        this.s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }
}
