package com.github.davidpotentini.controller.monitoramento;

import com.github.davidpotentini.dto.monitoramento.AplicacaoDTO;
import com.github.davidpotentini.dto.monitoramento.RodadaDTO;
import com.github.davidpotentini.service.monitoramento.MonitoramentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Monitoramento das incubadas da própria incubadora (usuário logado). O tenant vem do JWT. Aba
 * "Rodadas" (planejar/listar rodadas) e aba "Aplicações e pontuação" (cards por rodada e revisão).
 */
@RestController
@RequestMapping("/incubadora/monitoramento")
public class MonitoramentoController {

    private final MonitoramentoService service;

    public MonitoramentoController(MonitoramentoService service) {
        this.service = service;
    }

    @GetMapping("/rodadas")
    public List<RodadaDTO> listarRodadas() {
        return service.listarRodadas();
    }

    @PostMapping("/rodadas")
    @ResponseStatus(HttpStatus.CREATED)
    public RodadaDTO planejar(@Valid @RequestBody RodadaDTO dto) {
        return service.planejar(dto);
    }

    @PatchMapping("/rodadas/{rodCod}/concluir")
    public RodadaDTO concluir(@PathVariable Long rodCod) {
        return service.concluir(rodCod);
    }

    @GetMapping("/rodadas/{rodCod}/aplicacoes")
    public List<AplicacaoDTO> aplicacoes(@PathVariable Long rodCod) {
        return service.aplicacoes(rodCod);
    }

    @PutMapping("/rodadas/{rodCod}/empreendimentos/{empCod}/avaliacao")
    public AplicacaoDTO revisar(@PathVariable Long rodCod, @PathVariable Long empCod,
                                @Valid @RequestBody AplicacaoDTO dto) {
        return service.revisar(rodCod, empCod, dto);
    }
}
