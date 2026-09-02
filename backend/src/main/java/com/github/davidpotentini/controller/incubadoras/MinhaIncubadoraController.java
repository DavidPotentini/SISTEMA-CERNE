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

/**
 * Área da própria incubadora (usuário logado, não admin). Diferente de
 * {@link IncubadoraController} (admin, {@code /admin/incubadoras}), aqui a incubadora é a da
 * conta da sessão — sem id na URL. Basta estar autenticado. Só os dados da incubadora: a equipe
 * e os empreendimentos têm endpoints próprios ({@code /incubadora/equipe},
 * {@code /incubadora/empreendimentos}), cada card carregando de forma independente.
 */
@RestController
@RequestMapping("/incubadora")
public class MinhaIncubadoraController {

    private final IncubadoraService incubadoraService;

    public MinhaIncubadoraController(IncubadoraService incubadoraService) {
        this.incubadoraService = incubadoraService;
    }

    /** Dados da incubadora do usuário logado. */
    @GetMapping("/minha")
    public IncubadoraDTO minha() {
        return incubadoraService.minhaIncubadora(SessaoContext.contaAtual());
    }

    /** Edição da própria ficha institucional (só os campos editáveis pela incubadora). */
    @PutMapping("/minha")
    public IncubadoraDTO atualizarMinha(@Valid @RequestBody IncubadoraDTO dto) {
        return incubadoraService.atualizarMinha(SessaoContext.contaAtual(), dto);
    }
}
