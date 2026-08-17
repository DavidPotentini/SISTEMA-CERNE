package com.github.davidpotentini.controller.empreendimentos;

import com.github.davidpotentini.dto.empreendimentos.EmpreendimentoDTO;
import com.github.davidpotentini.dto.empreendimentos.PessoaEmpreendimentoDTO;
import com.github.davidpotentini.dto.empreendimentos.ResponsavelDTO;
import com.github.davidpotentini.service.empreendimentos.EmpreendimentoService;
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
 * Empreendimentos da própria incubadora (usuário logado, não admin). O tenant vem do JWT — basta
 * estar autenticado. Cada empreendimento tem suas pessoas (membros da startup) geridas no modal.
 */
@RestController
@RequestMapping("/incubadora/empreendimentos")
public class EmpreendimentoController {

    private final EmpreendimentoService service;

    public EmpreendimentoController(EmpreendimentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmpreendimentoDTO> listar() {
        return service.listar();
    }

    /** Candidatos a responsável interno (equipe da incubadora). */
    @GetMapping("/responsaveis")
    public List<ResponsavelDTO> listarResponsaveis() {
        return service.listarResponsaveis();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpreendimentoDTO criar(@Valid @RequestBody EmpreendimentoDTO dto) {
        return service.criar(dto);
    }

    @PutMapping("/{empCod}")
    public EmpreendimentoDTO editar(@PathVariable Long empCod,
                                    @Valid @RequestBody EmpreendimentoDTO dto) {
        return service.editar(empCod, dto);
    }

    @GetMapping("/{empCod}/pessoas")
    public List<PessoaEmpreendimentoDTO> listarPessoas(@PathVariable Long empCod) {
        return service.listarPessoas(empCod);
    }

    @PostMapping("/{empCod}/pessoas")
    @ResponseStatus(HttpStatus.CREATED)
    public PessoaEmpreendimentoDTO adicionarPessoa(@PathVariable Long empCod,
                                                   @Valid @RequestBody PessoaEmpreendimentoDTO dto) {
        return service.adicionarPessoa(empCod, dto);
    }

    /** Marca uma pessoa do empreendimento como contato principal. */
    @PatchMapping("/{empCod}/pessoas/{pseCod}/principal")
    public PessoaEmpreendimentoDTO definirPrincipal(@PathVariable Long empCod,
                                                    @PathVariable Long pseCod) {
        return service.definirPrincipal(empCod, pseCod);
    }
}
