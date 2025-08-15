package io.manurasahs.deltavault.port.adapter.clientrest.resources.service;

import static org.springframework.http.ResponseEntity.ok;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.manurasahs.deltavault.application.StorageService;
import io.manurasahs.deltavault.port.adapter.clientrest.resources.api.FileStorageApi;
import io.manurasahs.deltavault.port.adapter.clientrest.resources.model.SimpleInfoMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileStorageApiService implements FileStorageApi
{

    private final StorageService storageService;

    private final ObjectMapper objectMapper;

    public FileStorageApiService(StorageService storageService, ObjectMapper objectMapper)
    {
        this.storageService = storageService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<Map<String, Object>> downloadFile(String fileName, String version)
    {
        return null;
    }

    @Override
    public ResponseEntity<SimpleInfoMessage> upsertFile(String fileName, Map<String, Object> requestBody)
    {
        try
        {
            this.storageService.upload("my-test-bucket", fileName, this.objectMapper.writeValueAsBytes(requestBody));
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
        try
        {
            System.out.println(this.objectMapper.readValue(
                this.storageService.download("my-test-bucket", fileName),
                Map.class
            ));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return ok(new SimpleInfoMessage(STR."File \{fileName} uploaded successfully"));
    }
}
