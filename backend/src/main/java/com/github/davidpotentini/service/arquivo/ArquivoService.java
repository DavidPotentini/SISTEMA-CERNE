package com.github.davidpotentini.service.arquivo;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.arquivo.ArquivoDTO;
import com.github.davidpotentini.model.arquivo.ArquivoModel;
import com.github.davidpotentini.repository.arquivo.ArquivoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Upload e leitura de arquivos. O binário vai para o object storage (S3/MinIO) sob uma chave única; os
 * metadados ficam em {@code ARQUIVOS} (schema do tenant). A URL de download é temporária (presigned),
 * gerada a cada leitura — nunca persistida.
 */
@Service
public class ArquivoService {

    private final ArquivoRepository arquivos;
    private final S3Client s3;
    private final S3Presigner presigner;

    @Value("${storage.bucket}")
    private String bucket;

    public ArquivoService(ArquivoRepository arquivos, S3Client s3, S3Presigner presigner) {
        this.arquivos = arquivos;
        this.s3 = s3;
        this.presigner = presigner;
    }

    /** Sobe o arquivo para o storage e registra os metadados; devolve o DTO com a URL temporária. */
    @Transactional
    public ArquivoDTO upload(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraNegocioException("Envie um arquivo não vazio.");
        }
        String chave = UUID.randomUUID() + "-" + arquivo.getOriginalFilename();
        try {
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(chave)
                            .contentType(arquivo.getContentType())
                            .build(),
                    RequestBody.fromBytes(arquivo.getBytes()));
        } catch (IOException e) {
            throw new RegraNegocioException("Falha ao ler o arquivo enviado.");
        }

        ArquivoModel model = new ArquivoModel();
        model.setNomeOriginal(arquivo.getOriginalFilename());
        model.setStorageKey(chave);
        model.setContentType(arquivo.getContentType());
        model.setTamanhoBytes(arquivo.getSize());
        model.setDataUpload(LocalDateTime.now());
        arquivos.save(model);
        return toDTO(model);
    }

    /** Metadados + URL temporária de download de um arquivo. */
    @Transactional(readOnly = true)
    public ArquivoDTO buscar(Long arqCod) {
        ArquivoModel model = arquivos.findById(arqCod)
                .orElseThrow(() -> new NaoEncontradoException("Arquivo", arqCod));
        return toDTO(model);
    }

    private ArquivoDTO toDTO(ArquivoModel model) {
        return new ArquivoDTO(model.getArqCod(), model.getNomeOriginal(), model.getContentType(),
                model.getTamanhoBytes(), url(model.getStorageKey()));
    }

    /** URL de download temporária (15 min) para a chave de storage. */
    private String url(String chave) {
        if (chave == null) {
            return null;
        }
        GetObjectPresignRequest req = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(GetObjectRequest.builder().bucket(bucket).key(chave).build())
                .build();
        return presigner.presignGetObject(req).url().toString();
    }
}
