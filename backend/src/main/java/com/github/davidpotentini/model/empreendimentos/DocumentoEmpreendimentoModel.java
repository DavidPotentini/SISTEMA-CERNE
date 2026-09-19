package com.github.davidpotentini.model.empreendimentos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DOCUMENTOS_EMPREENDIMENTO")
@IdClass(DocumentoEmpreendimentoId.class)
@Getter
@Setter
public class DocumentoEmpreendimentoModel {

    @Id
    @Column(name = "EMP_COD")
    private Long empCod;

    @Id
    @Column(name = "ARQ_COD")
    private Long arqCod;
}
