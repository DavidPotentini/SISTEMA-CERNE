package com.github.davidpotentini.controller.ciclos;

import com.github.davidpotentini.comum.ciclo.EscopoCiclo;
import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.service.ciclos.MaterializacaoMetodologiaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * "Materializar a metodologia no ciclo" (o "Gerar do ciclo"): estrutura + indicadores + atividades no
 * ciclo em foco. {@code @EscopoCiclo}: materializar num ciclo em foco encerrado é barrado (só leitura →
 * 409); o GET do alvo é leitura, sempre permitido. Acionado pelo botão da tela de Metodologia.
 */
@RestController
@EscopoCiclo
@RequestMapping("/incubadora/materializacao-metodologia")
public class MaterializacaoMetodologiaController {

    private final MaterializacaoMetodologiaService service;

    public MaterializacaoMetodologiaController(MaterializacaoMetodologiaService service) {
        this.service = service;
    }

    /** Ciclo em foco que receberá a materialização (ou nada, se não houver). */
    @GetMapping("/alvo")
    public CicloDTO alvo() {
        return service.alvo();
    }

    /** Materializa a metodologia no ciclo em foco. */
    @PostMapping
    public CicloDTO materializarMetodologia() {
        return service.materializarMetodologia();
    }
}
