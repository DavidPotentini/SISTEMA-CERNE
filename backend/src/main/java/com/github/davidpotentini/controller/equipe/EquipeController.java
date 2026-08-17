package com.github.davidpotentini.controller.equipe;

import com.github.davidpotentini.dto.equipe.PessoaEquipeDTO;
import com.github.davidpotentini.service.equipe.EquipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Equipe vinculada da própria incubadora (usuário logado, não admin). O tenant vem do JWT — basta
 * estar autenticado. Endpoint próprio da tela "Minha Incubadora", carregado de forma independente
 * do card de dados e do de empreendimentos.
 */
@RestController
@RequestMapping("/incubadora/equipe")
public class EquipeController {

    private final EquipeService service;

    public EquipeController(EquipeService service) {
        this.service = service;
    }

    @GetMapping
    public List<PessoaEquipeDTO> listar() {
        return service.listar();
    }
}
