package com.github.davidpotentini.controller.painel;

import com.github.davidpotentini.dto.painel.PendenciaDTO;
import com.github.davidpotentini.service.painel.PendenciasService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/incubadora/pendencias")
public class PendenciasController {

    private final PendenciasService service;

    public PendenciasController(PendenciasService service) {
        this.service = service;
    }

    @GetMapping
    public List<PendenciaDTO> pendencias() {
        return service.pendencias();
    }
}
