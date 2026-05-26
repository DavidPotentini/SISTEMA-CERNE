package com.github.davidpotentini.cerne2.models.arquivo.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArquivosEvidenciasId implements Serializable {

    @Column(name = "EVD_COD")
    private Long evdCod;

    @Column(name = "ARQ_COD")
    private Long arqCod;
}
