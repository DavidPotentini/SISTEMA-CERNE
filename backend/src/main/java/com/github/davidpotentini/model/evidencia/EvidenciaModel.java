package com.github.davidpotentini.model.evidencia;

import com.github.davidpotentini.enums.EStatusEvidencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

/**
 * Uma <b>versão</b> da evidência (tabela {@code EVIDENCIAS}). O par ({@code evdCod}, {@code evdCodSeq})
 * é a chave: {@code evdCod} é o id lógico (igual em todas as versões da mesma evidência) e
 * {@code evdCodSeq} é o número da versão. Título, arquivo, atividade, responsável e status variam por
 * versão; a "evidência atual" é a de maior {@code evdCodSeq}.
 *
 * <p>Como a chave é <b>atribuída</b> pela aplicação (não {@code IDENTITY}), implementamos
 * {@link Persistable} devolvendo {@code isNew() == true} até o primeiro load/persist — assim o
 * {@code save()} faz {@code INSERT} direto, sem o {@code SELECT} que o Spring Data faria ao ver a
 * chave já preenchida.
 */
@Entity
@Table(name = "EVIDENCIAS")
@IdClass(EvidenciaId.class)
@Getter
@Setter
public class EvidenciaModel implements Persistable<EvidenciaId> {

    @Id
    @Column(name = "EVD_COD")
    private Long evdCod;

    @Id
    @Column(name = "EVD_COD_SEQ")
    private Integer evdCodSeq;

    @Column(name = "TITULO", nullable = false)
    private String titulo;

    @Column(name = "ATP_COD", nullable = false)
    private Long atpCod;

    @Column(name = "ARQ_COD")
    private Long arqCod;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private EStatusEvidencia status = EStatusEvidencia.PENDENTE_VALIDACAO;

    /** Motivo da rejeição, gravado ao solicitar correção; {@code null} nos demais estados. */
    @Column(name = "MOTIVO_CORRECAO")
    private String motivoCorrecao;

    @Column(name = "REG_PES_COD")
    private Long regPesCod;

    @Column(name = "DATA")
    private LocalDateTime data;

    @Transient
    private boolean novo = true;

    @Override
    public EvidenciaId getId() {
        return new EvidenciaId(evdCod, evdCodSeq);
    }

    @Override
    public boolean isNew() {
        return novo;
    }

    @PostLoad
    @PostPersist
    void marcarExistente() {
        this.novo = false;
    }
}
