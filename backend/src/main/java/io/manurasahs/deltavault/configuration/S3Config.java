package io.manurasahs.deltavault.configuration;

import java.net.URI;
import java.util.Optional;

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

    private final S3ConfigurationProperties s3ConfigurationProperties;

    public S3Config(S3ConfigurationProperties s3ConfigurationProperties)
    {
        this.s3ConfigurationProperties = s3ConfigurationProperties;
    }

    @Bean
    @Profile("local")
    public S3Client s3ClientLocalStack()
    {
        return S3Client.builder()
            .endpointOverride(URI.create(Optional.ofNullable(this.s3ConfigurationProperties.endpoint())
                .orElseThrow(() -> new IllegalStateException("Incorrect configuration. Endpoint not provided."))))
            .region(Region.of(this.s3ConfigurationProperties.region()))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    Optional.ofNullable(this.s3ConfigurationProperties.accessKey())
                        .orElseThrow(() -> new IllegalStateException("Incorrect configuration. Access key not provided.")),
                    Optional.ofNullable(this.s3ConfigurationProperties.secretKey())
                        .orElseThrow(() -> new IllegalStateException("Incorrect configuration. Secret key not provided."))
                )
            ))
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(this.s3ConfigurationProperties.pathStyle())
                .build())
            .build();
    }

    @Bean
    @Profile("!local")
    public S3Client s3ClientAws()
    {
        return S3Client.builder()
            .region(Region.of(this.s3ConfigurationProperties.region()))
            // todo configure real connection
            // Uses the default provider chain: env vars, profile file, EC2/ECS role, etc.
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(this.s3ConfigurationProperties.pathStyle())
                .build())
            .build();
    }
}
