package com.github.davidpotentini.controller.equipe;

import com.github.davidpotentini.dto.equipe.PessoaEquipeDTO;
import com.github.davidpotentini.dto.equipe.ResponsavelDTO;
import com.github.davidpotentini.service.equipe.EquipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/responsaveis")
    public List<ResponsavelDTO> listarResponsaveis() {
        return service.listarResponsaveis();
    }
}
