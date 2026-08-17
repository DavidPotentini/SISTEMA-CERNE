package com.github.davidpotentini.controller.planejamento;

import com.github.davidpotentini.dto.planejamento.AtividadePlanejadaDTO;
import com.github.davidpotentini.dto.planejamento.PlanProcessoDTO;
import com.github.davidpotentini.dto.planejamento.PlanejamentoAtualDTO;
import com.github.davidpotentini.dto.planejamento.PlanejamentoDTO;
import com.github.davidpotentini.service.planejamento.PlanejamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Planejamento institucional do ciclo ativo (tenant vem do JWT; basta estar autenticado). No máx. um
 * planejamento vigente por ciclo. A tela mostra a situação do ciclo ativo, gera o plano a partir de um
 * modelo publicado (substituindo o anterior) e permite ajustar/incluir atividades.
 */
@RestController
@RequestMapping("/incubadora/planejamento")
public class PlanejamentoController {

    private final PlanejamentoService service;

    public PlanejamentoController(PlanejamentoService service) {
        this.service = service;
    }

    /** Situação do ciclo ativo e o planejamento vigente (ou nada, se ainda não gerado). */
    @GetMapping
    public PlanejamentoAtualDTO atual() {
        return service.atual();
    }

    /** Gera o planejamento do ciclo ativo a partir do modelo (substitui o vigente, se houver). */
    @PostMapping("/gerar")
    @ResponseStatus(HttpStatus.CREATED)
    public PlanejamentoDTO gerar(@RequestParam Long modCod) {
        return service.gerarDeModelo(modCod);
    }

    /** Estrutura (processos/práticas da metodologia base) com as atividades planejadas. */
    @GetMapping("/estrutura")
    public List<PlanProcessoDTO> estrutura() {
        return service.estrutura();
    }

    @PostMapping("/praticas/{prtCod}/atividades")
    @ResponseStatus(HttpStatus.CREATED)
    public AtividadePlanejadaDTO adicionarComplementar(@PathVariable Long prtCod,
                                                       @Valid @RequestBody AtividadePlanejadaDTO dto) {
        return service.adicionarComplementar(prtCod, dto);
    }

    @PutMapping("/atividades/{atpCod}")
    public AtividadePlanejadaDTO ajustar(@PathVariable Long atpCod,
                                         @Valid @RequestBody AtividadePlanejadaDTO dto) {
        return service.ajustarAtividade(atpCod, dto);
    }

    /** Remove uma atividade complementar (as do modelo só são ajustadas). */
    @DeleteMapping("/atividades/{atpCod}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long atpCod) {
        service.removerComplementar(atpCod);
    }
}
