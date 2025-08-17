package io.manurasahs.deltavault.application.common;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public final class JsonMapper
{

    private final ObjectMapper mapper;

    public JsonMapper(ObjectMapper objectMapper)
    {
        this.mapper = objectMapper;
    }

    public <T> byte[] serializeToBytes(@Nonnull T object)
    {
        try
        {
            return this.mapper.writeValueAsBytes(object);
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    @Nonnull
    public JsonNode readTree(@Nonnull byte[] json)
    {
        try
        {
            return this.mapper.readTree(json);
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    @Nonnull
    public <T> JsonNode serialize(@Nonnull T object)
    {
        return this.mapper.valueToTree(object);
    }

    @Nonnull
    public <T> T deserialize(@Nonnull JsonNode json, @Nonnull TypeReference<T> typeReference)
    {
        try
        {
            return this.mapper.treeToValue(json, typeReference);
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
