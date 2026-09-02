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

/**
 * Registros de evidência (tenant vem do JWT). Cada evidência é versionada: a listagem mostra a versão
 * corrente; ABRIR devolve o histórico de versões (cada uma com seu motivo de correção, se houve);
 * registrar cria a versão 1 e corrigir gera a próxima versão (só quando a corrente está em correção
 * solicitada).
 */
@RestController
@EscopoCiclo
@RequestMapping("/incubadora/evidencias")
public class EvidenciaController {

    private final EvidenciaService service;

    public EvidenciaController(EvidenciaService service) {
        this.service = service;
    }

    /** Versão corrente de cada evidência (para a listagem, cards e filtro por status). */
    @GetMapping
    public List<EvidenciaDTO> listar() {
        return service.listar();
    }

    /** Atividades do plano vigente (lista plana com prática/processo) para os dropdowns do cadastro. */
    @GetMapping("/atividades")
    public List<AtividadeOpcaoDTO> atividades() {
        return service.atividadesDisponiveis();
    }

    /** Histórico da evidência: todas as versões em ordem, cada uma com seu motivo de correção (ABRIR). */
    @GetMapping("/{evdCod}")
    public List<EvidenciaDTO> historico(@PathVariable Long evdCod) {
        return service.historico(evdCod);
    }

    /** Registra uma nova evidência (versão 1). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenciaDTO registrar(@Valid @RequestBody EvidenciaDTO dto) {
        return service.registrar(dto);
    }

    /** Corrige a evidência gerando a próxima versão (mesmo id lógico). */
    @PostMapping("/{evdCod}/correcoes")
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenciaDTO corrigir(@PathVariable Long evdCod, @Valid @RequestBody EvidenciaDTO dto) {
        return service.corrigir(evdCod, dto);
    }

    /** Avalia a versão corrente (só se PENDENTE_VALIDACAO): validar ou solicitar correção (motivo obrigatório). */
    @PostMapping("/{evdCod}/avaliacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenciaDTO avaliar(@PathVariable Long evdCod, @Valid @RequestBody AvaliacaoDTO dto) {
        return service.avaliar(evdCod, dto.status(), dto.motivo());
    }
}
