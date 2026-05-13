package com.github.davidpotentini.cerne2.models.canvas;

import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "AMBIENTE_CANVAS")
@Table
@Getter
@Setter
public class AmbienteCanvasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AMBC_COD")
    private Long ambcCod;

    @Column(name = "DESCRICAO")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "INC_COD")
    private IncubadasModel incubadasModel;

    @OneToMany(mappedBy = "ambienteCanvasModel")
    private List<CustomerPersonasCanvasModel> customerPersonasCanvasModelList;

    @OneToMany(mappedBy = "ambienteCanvasModel")
    private List<ChannelImplementationCanvasModel> channelImplementationCanvasModelList;
}
