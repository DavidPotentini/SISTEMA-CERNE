package com.github.davidpotentini.controller.incubadoras;

import com.github.davidpotentini.comum.tenant.SessaoContext;
import com.github.davidpotentini.dto.incubadoras.IncubadoraDTO;
import com.github.davidpotentini.service.incubadoras.IncubadoraService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/incubadora")
public class MinhaIncubadoraController {

    private final IncubadoraService incubadoraService;

    public MinhaIncubadoraController(IncubadoraService incubadoraService) {
        this.incubadoraService = incubadoraService;
    }

    @GetMapping("/minha")
    public IncubadoraDTO minha() {
        return incubadoraService.minhaIncubadora(SessaoContext.contaAtual());
    }

    @PutMapping("/minha")
    public IncubadoraDTO atualizarMinha(@Valid @RequestBody IncubadoraDTO dto) {
        return incubadoraService.atualizarMinha(SessaoContext.contaAtual(), dto);
    }
}
