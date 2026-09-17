package com.github.davidpotentini.model.papeis;

import com.github.davidpotentini.enums.ENivel;
import com.github.davidpotentini.enums.ERecurso;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PAPEL_PERMISSOES")
@IdClass(PapelPermissaoId.class)
@Getter
@Setter
public class PapelPermissaoModel {

    @Id
    @Column(name = "PAP_COD")
    private Long papCod;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "RECURSO")
    private ERecurso recurso;

    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL", nullable = false)
    private ENivel nivel;
}
