package com.github.davidpotentini.cerne2.models.arquivo;


import com.github.davidpotentini.cerne2.models.arquivo.ids.ArquivosEvidenciasId;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.EvidenciasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.nio.channels.Pipe;

@Entity(name = "ARQUIVOS_EVIDENCIAS")
@Table
@Getter
@Setter
public class ArquivosEvidenciasModel {

    @EmbeddedId
    private ArquivosEvidenciasId arquivosEvidenciasId;

    @JoinColumn(name = "EVD_COD")
    @ManyToOne
    @MapsId("evdCod")
    private EvidenciasModel evidenciasModel;

    @JoinColumn(name = "ARQ_COD")
    @ManyToOne
    @MapsId("arqCod")
    private ArquivosModel arquivosModel;
}
