package io.manurasahs.deltavault.configuration;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "deltavault.s3")
public record S3ConfigurationProperties(
    @Nonnull String region,
    boolean pathStyle,
    @Nonnull String bucketName,
    @Nullable String endpoint,
    @Nullable String accessKey,
    @Nullable String secretKey
)
{
}
