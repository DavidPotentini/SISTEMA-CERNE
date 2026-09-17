package com.github.davidpotentini.model.evidencia;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/** {@code EVD_COD} = id lógico da evidência (repetido entre versões); {@code EVD_COD_SEQ} = número da versão. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EvidenciaId implements Serializable {
    private Long evdCod;
    private Integer evdCodSeq;
}
