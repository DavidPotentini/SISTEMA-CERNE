package com.github.davidpotentini.model.arquivo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Metadados de um arquivo enviado (tabela {@code ARQUIVOS}). O binário fica no object storage (S3/MinIO)
 * sob {@code storageKey}; aqui guardamos só o ponteiro e os metadados para gerar a URL de download.
 */
@Entity
@Table(name = "ARQUIVOS")
@Getter
@Setter
public class ArquivoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ARQ_COD")
    private Long arqCod;

    @Column(name = "NOME_ORIGINAL")
    private String nomeOriginal;

    @Column(name = "STORAGE_KEY")
    private String storageKey;

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @Column(name = "TAMANHO_BYTES")
    private Long tamanhoBytes;

    @Column(name = "DATA_UPLOAD")
    private LocalDateTime dataUpload;
}
