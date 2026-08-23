package com.github.davidpotentini.controller.indicador;

import com.github.davidpotentini.dto.indicador.IndicadorCicloDTO;
import com.github.davidpotentini.dto.indicador.PraticaOpcaoDTO;
import com.github.davidpotentini.service.indicador.IndicadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
 * Indicadores do ciclo ativo (tenant vem do JWT). Listagem da aba "Indicadores do ciclo"; "Gerar"
 * copia os indicadores da metodologia vigente; "Definir complementar" inclui um indicador manual.
 * {@code /vinculos} alimenta o seletor processo → prática do cadastro complementar.
 */
@RestController
@RequestMapping("/incubadora/indicadores")
public class IndicadorController {

    private final IndicadorService service;

    public IndicadorController(IndicadorService service) {
        this.service = service;
    }

    /** Indicadores do ciclo ativo (nome, origem, unidade, periodicidade, situação, vínculo CERNE). */
    @GetMapping
    public List<IndicadorCicloDTO> listar() {
        return service.listar();
    }

    /** Práticas da metodologia vigente para o seletor de vínculo do indicador complementar. */
    @GetMapping("/vinculos")
    public List<PraticaOpcaoDTO> vinculos() {
        return service.vinculos();
    }

    /** Gera (ou regenera) os indicadores do ciclo a partir da metodologia vigente. */
    @PostMapping("/gerar")
    public List<IndicadorCicloDTO> gerar() {
        return service.gerarDoCiclo();
    }

    /** Define um indicador complementar no ciclo ativo. */
    @PostMapping("/complementares")
    @ResponseStatus(HttpStatus.CREATED)
    public IndicadorCicloDTO definirComplementar(@Valid @RequestBody IndicadorCicloDTO dto) {
        return service.definirComplementar(dto);
    }

    /** Define (ou desvincula, com {@code respPesCod} ausente) o responsável pela apuração do indicador. */
    @PutMapping("/{indCod}/responsavel")
    public IndicadorCicloDTO definirResponsavel(@PathVariable Long indCod,
                                                @RequestParam(required = false) Long respPesCod) {
        return service.definirResponsavel(indCod, respPesCod);
    }
}
