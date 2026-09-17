package com.github.davidpotentini.controller.evidencia;

import com.github.davidpotentini.comum.ciclo.EscopoCiclo;
import com.github.davidpotentini.dto.evidencia.AtividadeOpcaoDTO;
import com.github.davidpotentini.dto.evidencia.AvaliacaoDTO;
import com.github.davidpotentini.dto.evidencia.EvidenciaDTO;
import com.github.davidpotentini.service.evidencia.EvidenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@EscopoCiclo
@RequestMapping("/incubadora/evidencias")
public class EvidenciaController {

    private final EvidenciaService service;

    public EvidenciaController(EvidenciaService service) {
        this.service = service;
    }

    @GetMapping
    public List<EvidenciaDTO> listar() {
        return service.listar();
    }

    @GetMapping("/atividades")
    public List<AtividadeOpcaoDTO> atividades() {
        return service.atividadesDisponiveis();
    }

    @GetMapping("/{evdCod}")
    public List<EvidenciaDTO> historico(@PathVariable Long evdCod) {
        return service.historico(evdCod);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenciaDTO registrar(@Valid @RequestBody EvidenciaDTO dto) {
        return service.registrar(dto);
    }

    @PostMapping("/{evdCod}/correcoes")
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenciaDTO corrigir(@PathVariable Long evdCod, @Valid @RequestBody EvidenciaDTO dto) {
        return service.corrigir(evdCod, dto);
    }

    @PostMapping("/{evdCod}/avaliacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenciaDTO avaliar(@PathVariable Long evdCod, @Valid @RequestBody AvaliacaoDTO dto) {
        return service.avaliar(evdCod, dto.status(), dto.motivo());
    }
}
