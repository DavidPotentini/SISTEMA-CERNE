package com.github.davidpotentini.cerne2.models.arquivo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "ARQUIVO")
@Table
@Getter
@Setter
public class ArquivosModel {

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

    @OneToMany(mappedBy = "arquivosModel")
    List<ArquivosIncubadasModel> arquivosIncubadasModelList;

    @OneToMany(mappedBy = "arquivosModel")
    List<ArquivosEvidenciasModel> arquivosEvidenciasModelList;
}
