package com.github.davidpotentini.cerne2.models.arquivo.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArquivosIcubadasId implements Serializable {

    @Column(name = "INC_COD")
    private Long incCod;

    @Column(name = "ARQ_COD")
    private Long arqCod;
}
