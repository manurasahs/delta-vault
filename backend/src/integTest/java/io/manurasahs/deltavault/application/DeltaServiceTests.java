package io.manurasahs.deltavault.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import io.manurasahs.deltavault.application.common.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// todo: create base integTest class
@SpringBootTest
public class DeltaServiceTests
{

    @Autowired
    private DeltaService deltaService;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    // todo: now it is Jackson testing, needed to enhance
    void shouldComputeDiffCorrectly()
    {
        // given:
        var firstJson = this.jsonMapper.serialize(
            Map.of(
                "name", "Simple json",
                "attributes", Map.of(
                    "first", 1,
                    "second", "2",
                    "third", 3.01F
                )
            )
        );
        var secondJson = this.jsonMapper.serialize(
            Map.of(
                "name", "Simple json new name",
                "attributes", Map.of(
                    "first", 1,
                    "second", "two",
                    "third", 3.0F
                )
            )
        );

        // when:
        var jsonPatch = this.deltaService.computeDuff(firstJson, secondJson);
        var jsonDiff = this.jsonMapper.deserialize(jsonPatch, new TypeReference<List<Map<String, Object>>>() {});

        // then:
        assertThat(jsonDiff).containsExactlyInAnyOrder(
            Map.of("op", "replace", "path", "/attributes/second", "value", "two"),
            Map.of("op", "replace", "path", "/attributes/third", "value", 3.0f),
            Map.of("op", "replace", "path", "/name", "value", "Simple json new name")
        );
    }
}
