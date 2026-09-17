package com.github.davidpotentini.model.papeis;

import com.github.davidpotentini.enums.ERecurso;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class PapelPermissaoId implements Serializable {
    private Long papCod;
    private ERecurso recurso;
}
