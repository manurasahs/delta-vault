package io.manurasahs.deltavault.application;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import jakarta.annotation.Nonnull;

public class DeltaReader
{

    @Nonnull
    public static JsonNode computeDiff(
        @Nonnull JsonNode oldContent,
        @Nonnull JsonNode newContent
    )
    {
        return JsonDiff.asJson(oldContent, newContent);
    }

    @Nonnull
    public static JsonNode restoreFile(@Nonnull JsonNode lastFull, @Nonnull List<JsonNode> jsonDiffs)
    {
        return jsonDiffs.stream()
            .filter(Objects::nonNull)
            .reduce(lastFull, (source, patch) -> JsonPatch.apply(patch, source));
    }
}
