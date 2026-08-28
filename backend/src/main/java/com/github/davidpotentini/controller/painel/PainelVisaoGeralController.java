package com.github.davidpotentini.controller.painel;

import com.github.davidpotentini.dto.painel.ResumoCicloDTO;
import com.github.davidpotentini.service.painel.PainelVisaoGeralService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Painel de visão geral da incubadora (usuário logado; tenant no JWT). Uma tela, um endpoint: o
 * resumo agregado do andamento do ciclo em foco (contagens + fluxo de processos).
 */
@RestController
@RequestMapping("/incubadora/painel-visao-geral")
public class PainelVisaoGeralController {

    private final PainelVisaoGeralService service;

    public PainelVisaoGeralController(PainelVisaoGeralService service) {
        this.service = service;
    }

    @GetMapping
    public ResumoCicloDTO resumo() {
        return service.resumo();
    }
}
