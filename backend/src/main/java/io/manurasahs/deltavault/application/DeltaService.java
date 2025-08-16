package io.manurasahs.deltavault.application;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import io.manurasahs.deltavault.application.common.JsonMapper;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public class DeltaService
{

    private final JsonMapper jsonMapper;

    public DeltaService(JsonMapper jsonMapper)
    {
        this.jsonMapper = jsonMapper;
    }

    @Nonnull
    public JsonNode computeDuff(
        @Nonnull JsonNode oldContent,
        @Nonnull JsonNode newContent
    )
    {
        return JsonDiff.asJson(oldContent, newContent);
    }

    @Nonnull
    public JsonNode restoreFile(@Nonnull JsonNode lastFull, @Nonnull List<byte[]> jsonDiffs)
    {
        return jsonDiffs.stream()
            .filter(Objects::nonNull)
            .map(this.jsonMapper::readTree)
            .reduce(lastFull, (source, patch) -> JsonPatch.apply(patch, source));
    }
}
