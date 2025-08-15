package io.manurasahs.deltavault.configuration;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class DynamoDBConfig
{

    @Bean
    @Profile("local")
    public DynamoDbClient dynamoClientLocalStack(
        @Value("${deltavault.dynamo.endpoint}") String endpoint,
        @Value("${deltavault.dynamo.region}") String region,
        @Value("${deltavault.dynamo.accessKey}") String accessKey,
        @Value("${deltavault.dynamo.secretKey}") String secretKey
    )
    {
        return DynamoDbClient.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
            .build();
    }

    @Bean
    @Profile("!local")
    public DynamoDbClient dynamoClientAws(
        @Value("${deltavault.dynamo.region}") String region
    )
    {
        return DynamoDbClient.builder()
            .region(Region.of(region))
            // todo configure real connection
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build();
    }

//    @Bean
//    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
//        return DynamoDbEnhancedClient.builder()
//            .dynamoDbClient(dynamoDbClient)
//            .build();
//    }
}
