package io.manurasahs.deltavault.configuration;

import java.net.URI;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDBConfig
{

    private final DynamoDBConfigurationProperties dynamoDBConfigurationProperties;

    public DynamoDBConfig(DynamoDBConfigurationProperties dynamoDBConfigurationProperties)
    {
        this.dynamoDBConfigurationProperties = dynamoDBConfigurationProperties;
    }

    @Bean
    @Profile("local")
    public DynamoDbClient dynamoClientLocalStack()
    {
        return DynamoDbClient.builder()
            .endpointOverride(URI.create(Optional.ofNullable(this.dynamoDBConfigurationProperties.endpoint())
                .orElseThrow(() -> new IllegalStateException("Incorrect configuration. Endpoint not provided."))))
            .region(Region.of(this.dynamoDBConfigurationProperties.region()))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                Optional.ofNullable(this.dynamoDBConfigurationProperties.accessKey())
                    .orElseThrow(() -> new IllegalStateException("Incorrect configuration. Access key not provided.")),
                Optional.ofNullable(this.dynamoDBConfigurationProperties.secretKey())
                    .orElseThrow(() -> new IllegalStateException("Incorrect configuration. Secret key not provided."))
            )))
            .build();
    }

    @Bean
    @Profile("!local")
    public DynamoDbClient dynamoClientAws()
    {
        return DynamoDbClient.builder()
            .region(Region.of(this.dynamoDBConfigurationProperties.region()))
            // todo configure real connection
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient)
    {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    }
}
