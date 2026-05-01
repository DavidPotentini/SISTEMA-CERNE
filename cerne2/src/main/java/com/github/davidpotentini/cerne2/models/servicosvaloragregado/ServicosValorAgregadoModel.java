package com.github.davidpotentini.cerne2.models.servicosvaloragregado;

import com.github.davidpotentini.cerne2.enums.EAtivoInativo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Table(name = "SERVICOS_VALOR_AGREGADO")
@Entity
@Getter
@Setter
public class ServicosValorAgregadoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SERV_COD")
    private Long servCod;

    @Column(name = "SERV_TITULO")
    private String servTitulo;

    @Column(name = "SERV_DESC")
    private String servDesc;

    @Column(name = "SERV_CUSTO")
    private BigDecimal servCusto;

    @Column(name = "SERV_COND_CONTRATACAO")
    private String servCondContratacao;

    @Column(name = "SERV_ANEXOS")
    private String servAnexos;

//    @Column(name = "SERV_STATUS")
//    private EAtivoInativo eAtivoInativo;
}
