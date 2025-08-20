package io.manurasahs.deltavault.configuration;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "deltavault.dynamo")
public record DynamoDBConfigurationProperties(
    @Nonnull String region,
    @Nullable String endpoint,
    @Nullable String accessKey,
    @Nullable String secretKey
)
{
}
