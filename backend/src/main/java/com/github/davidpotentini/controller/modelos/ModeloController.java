package com.github.davidpotentini.controller.modelos;

import com.github.davidpotentini.dto.modelos.AtividadeModeloDTO;
import com.github.davidpotentini.dto.modelos.ModeloDTO;
import com.github.davidpotentini.dto.modelos.ModeloProcessoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.service.modelos.ModeloService;
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
 * LEGADO — substituído pela metodologia unificada: as atividades passaram a viver na Metodologia
 * ({@code ATIVIDADES_METODOLOGIA}) e são materializadas no ciclo pelo "Gerar do ciclo". Esta camada de
 * Modelos foi desconectada da navegação (rota/menu comentados) e é mantida apenas para referência.
 *
 * <p>Modelos de planejamento da própria incubadora (tenant vem do JWT; basta estar autenticado). A tela
 * lista modelos, abre um editor com a estrutura herdada da metodologia e permite editar apenas as
 * atividades enquanto o modelo está em RASCUNHO. Publicar torna o modelo imutável.
 */
@RestController
@RequestMapping("/incubadora/modelos")
public class ModeloController {

    private final ModeloService service;

    public ModeloController(ModeloService service) {
        this.service = service;
    }

    @GetMapping
    public List<ModeloDTO> listar() {
        return service.listarModelos();
    }

    @GetMapping("/{modCod}")
    public ModeloDTO obter(@PathVariable Long modCod) {
        return service.obterModelo(modCod);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModeloDTO criar(@Valid @RequestBody ModeloDTO dto) {
        return service.criarModelo(dto);
    }

    @PutMapping("/{modCod}")
    public ModeloDTO editar(@PathVariable Long modCod, @Valid @RequestBody ModeloDTO dto) {
        return service.editarModelo(modCod, dto);
    }

    /** Publica o modelo (imutável a partir daqui). */
    @PostMapping("/{modCod}/publicar")
    public ModeloDTO publicar(@PathVariable Long modCod) {
        return service.publicarModelo(modCod);
    }

    /** Estrutura (processos/práticas ATIVOS da versão base) com as atividades do modelo. */
    @GetMapping("/{modCod}/estrutura")
    public List<ModeloProcessoDTO> estrutura(@PathVariable Long modCod) {
        return service.estruturaModelo(modCod);
    }

    @PostMapping("/{modCod}/praticas/{prtCod}/atividades")
    @ResponseStatus(HttpStatus.CREATED)
    public AtividadeModeloDTO adicionarAtividade(@PathVariable Long modCod, @PathVariable Long prtCod,
                                                 @Valid @RequestBody AtividadeModeloDTO dto) {
        return service.adicionarAtividade(modCod, prtCod, dto);
    }

    @PutMapping("/{modCod}/atividades/{atmCod}")
    public AtividadeModeloDTO editarAtividade(@PathVariable Long modCod, @PathVariable Long atmCod,
                                              @Valid @RequestBody AtividadeModeloDTO dto) {
        return service.editarAtividade(modCod, atmCod, dto);
    }

    /** Ativa/inativa a atividade. */
    @PatchMapping("/{modCod}/atividades/{atmCod}/situacao")
    public AtividadeModeloDTO alterarSituacaoAtividade(@PathVariable Long modCod, @PathVariable Long atmCod,
                                                       @RequestParam EAtivoInativo situacao) {
        return service.alterarSituacaoAtividade(modCod, atmCod, situacao);
    }
}
