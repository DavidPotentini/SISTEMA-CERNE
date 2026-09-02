package com.github.davidpotentini.controller.metodologia;

import com.github.davidpotentini.dto.metodologia.AgrupamentoDTO;
import com.github.davidpotentini.dto.metodologia.AtividadeMetodologiaDTO;
import com.github.davidpotentini.dto.metodologia.PraticaDTO;
import com.github.davidpotentini.dto.metodologia.ProcessoDTO;
import com.github.davidpotentini.dto.metodologia.IndicadorDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.service.metodologia.MetodologiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
 * Metodologia da própria incubadora (usuário logado, não admin). O tenant vem do JWT — basta estar
 * autenticado. Documento vivo, sem versionamento: as abas editam a metodologia direto.
 */
@RestController
@RequestMapping("/incubadora/metodologia")
public class MetodologiaController {

    private final MetodologiaService service;

    public MetodologiaController(MetodologiaService service) {
        this.service = service;
    }

    @GetMapping("/processos")
    public List<ProcessoDTO> listarProcessos() {
        return service.listarProcessos();
    }

    @PostMapping("/processos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProcessoDTO criarProcesso(@Valid @RequestBody ProcessoDTO dto) {
        return service.criarProcesso(dto);
    }

    @PutMapping("/processos/{prcCod}")
    public ProcessoDTO editarProcesso(@PathVariable Long prcCod, @Valid @RequestBody ProcessoDTO dto) {
        return service.editarProcesso(prcCod, dto);
    }

    /** Reordena os processos (arrastar-e-soltar): o corpo é a sequência de {@code prcCod}. */
    @PutMapping("/processos/ordem")
    public List<ProcessoDTO> reordenarProcessos(@RequestBody List<Long> prcCods) {
        return service.reordenarProcessos(prcCods);
    }

    /** Ativa/inativa o processo (inativo continua visível, mas fora da geração do planejamento). */
    @PatchMapping("/processos/{prcCod}/situacao")
    public ProcessoDTO alterarSituacaoProcesso(@PathVariable Long prcCod,
                                               @RequestParam EAtivoInativo situacao) {
        return service.alterarSituacaoProcesso(prcCod, situacao);
    }

    @PostMapping("/processos/{prcCod}/praticas")
    @ResponseStatus(HttpStatus.CREATED)
    public PraticaDTO adicionarPratica(@PathVariable Long prcCod, @Valid @RequestBody PraticaDTO dto) {
        return service.adicionarPratica(prcCod, dto);
    }

    @PutMapping("/processos/{prcCod}/praticas/{prtCod}")
    public PraticaDTO editarPratica(@PathVariable Long prcCod, @PathVariable Long prtCod,
                                    @Valid @RequestBody PraticaDTO dto) {
        return service.editarPratica(prcCod, prtCod, dto);
    }

    @PatchMapping("/processos/{prcCod}/praticas/{prtCod}/situacao")
    public PraticaDTO alterarSituacaoPratica(@PathVariable Long prcCod, @PathVariable Long prtCod,
                                             @RequestParam EAtivoInativo situacao) {
        return service.alterarSituacaoPratica(prcCod, prtCod, situacao);
    }

    /** Reordena as práticas de um processo (arrastar-e-soltar): o corpo é a sequência de {@code prtCod}. */
    @PutMapping("/processos/{prcCod}/praticas/ordem")
    public List<ProcessoDTO> reordenarPraticas(@PathVariable Long prcCod, @RequestBody List<Long> prtCods) {
        return service.reordenarPraticas(prcCod, prtCods);
    }

    // ---- indicadores ----

    @GetMapping("/indicadores")
    public List<IndicadorDTO> listarIndicadores() {
        return service.listarIndicadores();
    }

    @PostMapping("/indicadores")
    @ResponseStatus(HttpStatus.CREATED)
    public IndicadorDTO criarIndicador(@Valid @RequestBody IndicadorDTO dto) {
        return service.criarIndicador(dto);
    }

    @PutMapping("/indicadores/{inmCod}")
    public IndicadorDTO editarIndicador(@PathVariable Long inmCod, @Valid @RequestBody IndicadorDTO dto) {
        return service.editarIndicador(inmCod, dto);
    }

    /** Ativa/inativa o indicador. */
    @PatchMapping("/indicadores/{inmCod}/situacao")
    public IndicadorDTO alterarSituacaoIndicador(@PathVariable Long inmCod,
                                                 @RequestParam EAtivoInativo situacao) {
        return service.alterarSituacaoIndicador(inmCod, situacao);
    }

    // ---- agrupamentos ----

    @GetMapping("/agrupamentos")
    public List<AgrupamentoDTO> listarAgrupamentos() {
        return service.listarAgrupamentos();
    }

    @PostMapping("/praticas/{prtCod}/agrupamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public AgrupamentoDTO adicionarAgrupamento(@PathVariable Long prtCod,
                                               @Valid @RequestBody AgrupamentoDTO dto) {
        return service.adicionarAgrupamento(prtCod, dto);
    }

    @PutMapping("/agrupamentos/{agrCod}")
    public AgrupamentoDTO editarAgrupamento(@PathVariable Long agrCod,
                                            @Valid @RequestBody AgrupamentoDTO dto) {
        return service.editarAgrupamento(agrCod, dto);
    }

    /** Reordena os agrupamentos de uma prática (arrastar-e-soltar): o corpo é a sequência de {@code agrCod}. */
    @PutMapping("/praticas/{prtCod}/agrupamentos/ordem")
    public List<AgrupamentoDTO> reordenarAgrupamentos(@PathVariable Long prtCod,
                                                      @RequestBody List<Long> agrCods) {
        return service.reordenarAgrupamentos(prtCod, agrCods);
    }

    /** Ativa/inativa o agrupamento. */
    @PatchMapping("/agrupamentos/{agrCod}/situacao")
    public AgrupamentoDTO alterarSituacaoAgrupamento(@PathVariable Long agrCod,
                                                     @RequestParam EAtivoInativo situacao) {
        return service.alterarSituacaoAgrupamento(agrCod, situacao);
    }

    /** Exclui o agrupamento (bloqueado se ainda tiver atividades). */
    @DeleteMapping("/agrupamentos/{agrCod}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirAgrupamento(@PathVariable Long agrCod) {
        service.excluirAgrupamento(agrCod);
    }

    // ---- atividades ----

    @GetMapping("/atividades")
    public List<AtividadeMetodologiaDTO> listarAtividades() {
        return service.listarAtividades();
    }

    @PostMapping("/atividades")
    @ResponseStatus(HttpStatus.CREATED)
    public AtividadeMetodologiaDTO criarAtividade(@Valid @RequestBody AtividadeMetodologiaDTO dto) {
        return service.criarAtividade(dto);
    }

    @PutMapping("/atividades/{ameCod}")
    public AtividadeMetodologiaDTO editarAtividade(@PathVariable Long ameCod,
                                                   @Valid @RequestBody AtividadeMetodologiaDTO dto) {
        return service.editarAtividade(ameCod, dto);
    }

    /** Reordena as atividades de uma prática (arrastar-e-soltar): o corpo é a sequência de {@code ameCod}. */
    @PutMapping("/praticas/{prtCod}/atividades/ordem")
    public List<AtividadeMetodologiaDTO> reordenarAtividades(@PathVariable Long prtCod,
                                                             @RequestBody List<Long> ameCods) {
        return service.reordenarAtividades(prtCod, ameCods);
    }

    /** Ativa/inativa a atividade. */
    @PatchMapping("/atividades/{ameCod}/situacao")
    public AtividadeMetodologiaDTO alterarSituacaoAtividade(@PathVariable Long ameCod,
                                                            @RequestParam EAtivoInativo situacao) {
        return service.alterarSituacaoAtividade(ameCod, situacao);
    }

    /** Marca/desmarca a atividade como "da incubada". */
    @PatchMapping("/atividades/{ameCod}/por-empreendimento")
    public AtividadeMetodologiaDTO alterarPorEmpreendimentoAtividade(@PathVariable Long ameCod,
                                                                     @RequestParam boolean valor) {
        return service.alterarPorEmpreendimentoAtividade(ameCod, valor);
    }

    /** Exclui a atividade-padrão (só a metodologia; o que já foi materializado no ciclo permanece). */
    @DeleteMapping("/atividades/{ameCod}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirAtividade(@PathVariable Long ameCod) {
        service.excluirAtividade(ameCod);
    }
}
