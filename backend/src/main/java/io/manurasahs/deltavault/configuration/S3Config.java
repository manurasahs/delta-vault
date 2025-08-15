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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3Config
{

    @Bean
    @Profile("local")
    public S3Client s3ClientLocalStack(
        @Value("${deltavault.s3.endpoint}") String endpoint,
        @Value("${deltavault.s3.region}") String region,
        @Value("${deltavault.s3.accessKey}") String accessKey,
        @Value("${deltavault.s3.secretKey}") String secretKey,
        @Value("${deltavault.s3.pathStyle:true}") boolean pathStyle
    )
    {
        return S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(pathStyle)
                .build())
            .build();
    }

    @Bean
    @Profile("!local")
    public S3Client s3ClientAws(
        @Value("${app.s3.region}") String region,
        @Value("${app.s3.pathStyle:false}") boolean pathStyle
    )
    {
        return S3Client.builder()
            .region(Region.of(region))
            // todo configure real connection
            // Uses the default provider chain: env vars, profile file, EC2/ECS role, etc.
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(pathStyle)
                .build())
            .build();
    }
}
