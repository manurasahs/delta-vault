package io.manurasahs.deltavault.port.adapter.awss3;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import io.manurasahs.deltavault.application.StorageService;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3StorageService implements StorageService
{

    private final S3Client s3Client;

    private final DynamoDbClient dynamoDbClient;

    public S3StorageService(S3Client s3Client, DynamoDbClient dynamoDbClient)
    {
        this.s3Client = s3Client;
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void upload(@Nonnull String bucket, @Nonnull String key, @Nonnull byte[] bytes)
    {
        var itemValues = Map.of(
            "fileId", AttributeValue.builder().s("qwe").build(),
            "version", AttributeValue.builder().n("123").build()
        );
        var request = PutItemRequest.builder()
            .tableName("FileMetadata")
            .item(itemValues)
            .build();
        PutItemResponse response = this.dynamoDbClient.putItem(request);
        System.out.println(STR."FileMetadata was successfully updated. The response is \{response}");
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
        var keyToGet = Map.of(
            "fileId", AttributeValue.builder().s("qwe").build(),
            "version", AttributeValue.builder().n("123").build()
        );

        GetItemRequest request = GetItemRequest.builder()
            .key(keyToGet)
            .tableName("FileMetadata")
            .build();
        var returnedItem = this.dynamoDbClient.getItem(request).item();
        if (returnedItem.isEmpty())
        {
            System.out.format("No item found with the key %s!\n", key);
        } else
        {
            var keys = returnedItem.keySet();
            System.out.println("Amazon DynamoDB table attributes: \n");
            for (String key1 : keys)
            {
                System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
            }
        }
        return this.s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build()).readAllBytes();
    }

    public void delete(String bucket, String key)
    {
        this.s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }
}
