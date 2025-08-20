package io.manurasahs.deltavault.port.adapter.clientrest.resources.service;

import static org.springframework.http.ResponseEntity.ok;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import io.manurasahs.deltavault.application.S3StorageService;
import io.manurasahs.deltavault.application.common.JsonMapper;
import io.manurasahs.deltavault.port.adapter.clientrest.resources.api.FileStorageApi;
import io.manurasahs.deltavault.port.adapter.clientrest.resources.model.SimpleInfoMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileStorageApiService implements FileStorageApi
{

    private final S3StorageService s3StorageService;

    private final JsonMapper jsonMapper;

    public FileStorageApiService(S3StorageService s3StorageService, JsonMapper jsonMapper)
    {
        this.s3StorageService = s3StorageService;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public ResponseEntity<Map<String, Object>> downloadFile(String fileName, String version)
    {
        return ok(
            this.jsonMapper.deserialize(
                this.s3StorageService.download(fileName),
                new TypeReference<>() {}
            )
        );
    }

    @Override
    public ResponseEntity<SimpleInfoMessage> upsertFile(String fileName, Map<String, Object> requestBody)
    {
        this.s3StorageService.upload(fileName, requestBody);
        return ok(new SimpleInfoMessage(STR."File \{fileName} uploaded successfully"));
    }
}
