package com.github.davidpotentini.controller.ciclos;

import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.service.ciclos.CicloService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Ciclos da própria incubadora (usuário logado, não admin). O tenant vem do JWT — basta estar
 * autenticado. Card da tela "Minha Incubadora". Encerrar é ação da própria tela de Ciclos, guardada
 * pelas pendências do ciclo (ver {@link com.github.davidpotentini.service.ciclos.CicloService}).
 */
@RestController
@RequestMapping("/incubadora/ciclos")
public class CicloController {

    private final CicloService service;

    public CicloController(CicloService service) {
        this.service = service;
    }

    @GetMapping
    public List<CicloDTO> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CicloDTO criar(@Valid @RequestBody CicloDTO dto) {
        return service.criar(dto);
    }

    /** Põe o ciclo em foco (o refletido nas telas). */
    @PatchMapping("/{cicCod}/foco")
    public CicloDTO porEmFoco(@PathVariable Long cicCod) {
        return service.porEmFoco(cicCod);
    }

    /** Encerra o ciclo ativo (irreversível); só passa sem pendências em aberto. */
    @PatchMapping("/{cicCod}/encerramento")
    public CicloDTO encerrar(@PathVariable Long cicCod) {
        return service.encerrar(cicCod);
    }
}
