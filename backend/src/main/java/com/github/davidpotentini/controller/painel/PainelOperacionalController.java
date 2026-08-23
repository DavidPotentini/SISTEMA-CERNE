package com.github.davidpotentini.controller.painel;

import com.github.davidpotentini.dto.painel.PendenciaDTO;
import com.github.davidpotentini.service.painel.PainelOperacionalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Painel Operacional da incubadora (usuário logado; tenant no JWT). Uma tela, um endpoint: a lista
 * única de pendências do ciclo ativo (atividades em aberto/atrasadas, evidências com correção
 * solicitada e metas de indicadores vencidas). O front agrupa por tipo e conta.
 */
@RestController
@RequestMapping("/incubadora/painel-operacional")
public class PainelOperacionalController {

    private final PainelOperacionalService service;

    public PainelOperacionalController(PainelOperacionalService service) {
        this.service = service;
    }

    @GetMapping
    public List<PendenciaDTO> pendencias() {
        return service.pendencias();
    }
}
