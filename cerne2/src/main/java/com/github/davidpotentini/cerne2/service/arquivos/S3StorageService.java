package com.github.davidpotentini.cerne2.service.arquivos;

import com.github.davidpotentini.cerne2.configuration.multitenancy.TenantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3StorageService{

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucket;

    public S3StorageService(S3Client s3Client,
                            S3Presigner s3Presigner,
                            @Value("${storage.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucket = bucket;
    }

    public String upload(MultipartFile file){
        String tenant = TenantContext.get();

        String key = "tenants/%s/%s-%s".formatted(tenant, UUID.randomUUID(), file.getOriginalFilename());

        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        }catch (IOException e){
            throw new RuntimeException("Falha ao ler o arquivo enviado", e);
        }

        return key;
    }

    public String urlTemporaria(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.
                builder().
                bucket(bucket).
                key(key).
                build();

        GetObjectPresignRequest getObjectPresignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedGetObjectRequest =
                s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }

    public void delete(String key){
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket).
                key(key).
                build());
    }
}
