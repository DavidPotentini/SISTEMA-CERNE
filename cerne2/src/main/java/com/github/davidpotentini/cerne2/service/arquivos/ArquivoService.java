package com.github.davidpotentini.cerne2.service.arquivos;

import com.github.davidpotentini.cerne2.dto.arquivo.ArquivoDTO;
import com.github.davidpotentini.cerne2.mapper.arquivo.ArquivosMapper;
import com.github.davidpotentini.cerne2.models.arquivo.ArquivosModel;
import com.github.davidpotentini.cerne2.repository.arquivo.ArquivoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ArquivoService {

    private final ArquivosMapper arquivosMapper;
    private final ArquivoRepository arquivoRepository;
    private final S3StorageService s3StorageService;

    public ArquivoService(ArquivosMapper arquivosMapper, ArquivoRepository arquivoRepository, S3StorageService s3StorageService) {
        this.arquivosMapper = arquivosMapper;
        this.arquivoRepository = arquivoRepository;
        this.s3StorageService = s3StorageService;
    }

    public ArquivosModel findById(Long arqCod){
        return arquivoRepository.findById(arqCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public ArquivoDTO upload(MultipartFile file){
        String key = s3StorageService.upload(file);

        ArquivosModel arquivosModel = new ArquivosModel();

        arquivosModel.setStorageKey(key);
        arquivosModel.setNomeOriginal(file.getName());
        arquivosModel.setContentType(file.getContentType());
        arquivosModel.setTamanhoBytes(file.getSize());
        arquivosModel.setDataUpload(LocalDateTime.now());

        return arquivosMapper.toDTO(arquivoRepository.save(arquivosModel));
    }

    public String gerarUrlDownload(Long arqCod){
        ArquivosModel arquivosModel = arquivoRepository.findById(arqCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return s3StorageService.urlTemporaria(arquivosModel.getStorageKey());
    }

    public void deletar(Long arqCod){
        ArquivosModel arquivosModel = arquivoRepository.findById(arqCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        s3StorageService.delete(arquivosModel.getStorageKey());

        arquivoRepository.deleteById(arqCod);
    }
}
