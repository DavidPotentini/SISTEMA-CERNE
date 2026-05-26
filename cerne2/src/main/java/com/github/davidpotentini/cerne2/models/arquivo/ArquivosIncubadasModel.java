package com.github.davidpotentini.cerne2.models.arquivo;

import com.github.davidpotentini.cerne2.models.arquivo.ids.ArquivosIcubadasId;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ARQUIVOS_INCUBADAS")
@Table
@Getter
@Setter
public class ArquivosIncubadasModel {

    @EmbeddedId
    private ArquivosIcubadasId arquivosIcubadasId;

    @JoinColumn(name = "INC_COD")
    @ManyToOne
    @MapsId("incCod")
    private IncubadasModel incubadasModel;

    @JoinColumn(name = "ARQ_COD")
    @ManyToOne
    @MapsId("arqCod")
    private ArquivosModel arquivosModel;
}
