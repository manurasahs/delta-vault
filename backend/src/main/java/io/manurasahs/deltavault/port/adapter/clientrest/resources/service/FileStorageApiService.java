package io.manurasahs.deltavault.port.adapter.clientrest.resources.service;

import static org.springframework.http.ResponseEntity.ok;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import io.manurasahs.deltavault.application.StorageService;
import io.manurasahs.deltavault.application.common.JsonMapper;
import io.manurasahs.deltavault.port.adapter.clientrest.resources.api.FileStorageApi;
import io.manurasahs.deltavault.port.adapter.clientrest.resources.model.SimpleInfoMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileStorageApiService implements FileStorageApi
{

    private final StorageService storageService;

    private final JsonMapper jsonMapper;

    public FileStorageApiService(StorageService storageService, JsonMapper jsonMapper)
    {
        this.storageService = storageService;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public ResponseEntity<Map<String, Object>> downloadFile(String fileName, String version)
    {
        return ok(
            this.jsonMapper.deserialize(
                this.storageService.download(fileName),
                new TypeReference<>() {}
            )
        );
    }

    @Override
    public ResponseEntity<SimpleInfoMessage> upsertFile(String fileName, Map<String, Object> requestBody)
    {
        this.storageService.upload(fileName, requestBody);
        return ok(new SimpleInfoMessage(STR."File \{fileName} uploaded successfully"));
    }
}
