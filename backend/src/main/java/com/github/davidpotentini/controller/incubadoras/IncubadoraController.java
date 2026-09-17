package com.github.davidpotentini.controller.incubadoras;

import com.github.davidpotentini.comum.autorizacao.RequerAdmin;
import com.github.davidpotentini.dto.incubadoras.IncubadoraDTO;
import com.github.davidpotentini.dto.incubadoras.IncubadoraResumoDTO;
import com.github.davidpotentini.enums.EStatusIncubadora;
import com.github.davidpotentini.service.incubadoras.IncubadoraService;
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

@RestController
@RequestMapping("/admin/incubadoras")
@RequerAdmin
public class IncubadoraController {

    private final IncubadoraService service;

    public IncubadoraController(IncubadoraService service) {
        this.service = service;
    }

    @GetMapping
    public List<IncubadoraResumoDTO> listar(@RequestParam(required = false) String nome,
                                            @RequestParam(required = false) EStatusIncubadora status) {
        return service.listar(nome, status);
    }

    @GetMapping("/contagem-ativas")
    public long contarAtivas() {
        return service.contarAtivas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncubadoraDTO criar(@Valid @RequestBody IncubadoraDTO dto) {
        return service.salvar(dto, null);
    }

    @GetMapping("/{id}")
    public IncubadoraDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public IncubadoraDTO salvar(@PathVariable Long id, @Valid @RequestBody IncubadoraDTO dto) {
        return service.salvar(dto, id);
    }

    @PatchMapping("/{id}/status")
    public IncubadoraDTO alternarStatus(@PathVariable Long id) {
        return service.alternarStatus(id);
    }
}
