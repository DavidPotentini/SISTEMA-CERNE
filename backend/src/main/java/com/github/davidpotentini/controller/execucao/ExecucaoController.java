package com.github.davidpotentini.controller.execucao;

import com.github.davidpotentini.comum.ciclo.EscopoCiclo;
import com.github.davidpotentini.dto.execucao.MudarStatusAtividadeDTO;
import com.github.davidpotentini.service.execucao.ExecucaoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Acompanhamento de execução (tenant vem do JWT). Muda o status de uma atividade planejada; concluir
 * exige que todas as evidências da atividade estejam validadas (regra em {@link ExecucaoService}).
 */
@RestController
@EscopoCiclo
@RequestMapping("/incubadora/execucao")
public class ExecucaoController {

    private final ExecucaoService service;

    public ExecucaoController(ExecucaoService service) {
        this.service = service;
    }

    /** Muda o estado de execução da atividade (planejada, em andamento ou concluída). */
    @PutMapping("/atividades/{atpCod}/status")
    public void mudarStatus(@PathVariable Long atpCod, @Valid @RequestBody MudarStatusAtividadeDTO dto) {
        service.mudarStatus(atpCod, dto.status());
    }
}
