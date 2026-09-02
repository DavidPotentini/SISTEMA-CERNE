package com.github.davidpotentini.controller.planejamento;

import com.github.davidpotentini.comum.ciclo.EscopoCiclo;
import com.github.davidpotentini.dto.planejamento.AtividadePlanejadaDTO;
import com.github.davidpotentini.dto.planejamento.PlanProcessoDTO;
import com.github.davidpotentini.dto.planejamento.PlanejamentoAtualDTO;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Planejamento institucional do ciclo ativo (tenant vem do JWT; basta estar autenticado). No máx. um
 * planejamento vigente por ciclo. O plano é materializado no "Gerar do ciclo" (Metodologia); esta tela
 * mostra a situação do ciclo ativo e permite ajustar/incluir/remover atividades.
 */
@RestController
@EscopoCiclo
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

    /** Estrutura (processos/práticas do ciclo) com as atividades planejadas. */
    @GetMapping("/estrutura")
    public List<PlanProcessoDTO> estrutura() {
        return service.estrutura();
    }

    @PostMapping("/praticas/{prtcCod}/atividades")
    @ResponseStatus(HttpStatus.CREATED)
    public AtividadePlanejadaDTO adicionarComplementar(@PathVariable Long prtcCod,
                                                       @Valid @RequestBody AtividadePlanejadaDTO dto) {
        return service.adicionarComplementar(prtcCod, dto);
    }

    /** Reordena as atividades de uma prática (arrastar-e-soltar): o corpo é a sequência de {@code atpCod}. */
    @PutMapping("/praticas/{prtcCod}/atividades/ordem")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reordenarAtividades(@PathVariable Long prtcCod, @RequestBody List<Long> atpCods) {
        service.reordenarAtividades(prtcCod, atpCods);
    }

    /** Reordena os agrupamentos de uma prática (arrastar-e-soltar): o corpo é a sequência de {@code agrcCod}. */
    @PutMapping("/praticas/{prtcCod}/agrupamentos/ordem")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reordenarAgrupamentos(@PathVariable Long prtcCod, @RequestBody List<Long> agrcCods) {
        service.reordenarAgrupamentos(prtcCod, agrcCods);
    }

    @PutMapping("/atividades/{atpCod}")
    public AtividadePlanejadaDTO ajustar(@PathVariable Long atpCod,
                                         @Valid @RequestBody AtividadePlanejadaDTO dto) {
        return service.ajustarAtividade(atpCod, dto);
    }

    /** Exclui uma atividade do plano (do modelo ou complementar), salvo se tiver evidências. */
    @DeleteMapping("/atividades/{atpCod}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long atpCod) {
        service.removerAtividade(atpCod);
    }
}
