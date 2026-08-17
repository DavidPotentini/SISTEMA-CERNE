package com.github.davidpotentini.controller.metodologia;

import com.github.davidpotentini.dto.metodologia.PraticaDTO;
import com.github.davidpotentini.dto.metodologia.ProcessoDTO;
import com.github.davidpotentini.dto.metodologia.IndicadorDTO;
import com.github.davidpotentini.dto.metodologia.VersaoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.service.metodologia.MetodologiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
 * autenticado. Aba "Processos e Práticas": o front busca a versão vigente e envia o {@code verCod}
 * nas operações de processo.
 */
@RestController
@RequestMapping("/incubadora/metodologia")
public class MetodologiaController {

    private final MetodologiaService service;

    public MetodologiaController(MetodologiaService service) {
        this.service = service;
    }

    /** Versão de trabalho (RASCUNHO) — a que as abas editam; criada na primeira vez, se necessário. */
    @GetMapping("/versoes/trabalho")
    public VersaoDTO versaoDeTrabalho() {
        return service.versaoDeTrabalho();
    }

    /** Histórico de publicações (VIGENTE + HISTORICA), mais recentes primeiro. */
    @GetMapping("/versoes")
    public List<VersaoDTO> listarVersoes() {
        return service.listarVersoes();
    }

    /** Publica o rascunho como uma nova versão (clona a árvore); só se houver alterações pendentes. */
    @PostMapping("/versoes/publicar")
    public VersaoDTO publicar() {
        return service.publicar();
    }

    @GetMapping("/processos")
    public List<ProcessoDTO> listarProcessos(@RequestParam Long verCod) {
        return service.listarProcessos(verCod);
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

    /** Ativa/inativa o processo (inativo continua visível, mas fora da criação de modelos). */
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

    // ---- indicadores ----

    @GetMapping("/indicadores")
    public List<IndicadorDTO> listarIndicadores(@RequestParam Long verCod) {
        return service.listarIndicadores(verCod);
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
}
