package com.github.davidpotentini.controller.indicador;

import com.github.davidpotentini.dto.indicador.MetaDTO;
import com.github.davidpotentini.service.indicador.MetaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Metas (períodos) de um indicador do ciclo (tenant vem do JWT). Cada período tem a meta estipulada e
 * a janela de apuração; o cadastro é manual.
 */
@RestController
@RequestMapping("/incubadora/indicadores/{indCod}/metas")
public class MetaController {

    private final MetaService service;

    public MetaController(MetaService service) {
        this.service = service;
    }

    /** Períodos do indicador, em ordem do início de apuração. */
    @GetMapping
    public List<MetaDTO> listar(@PathVariable Long indCod) {
        return service.listar(indCod);
    }

    /** Cadastra um período (meta) do indicador. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MetaDTO criar(@PathVariable Long indCod, @Valid @RequestBody MetaDTO dto) {
        return service.criar(indCod, dto);
    }

    /** Edita um período do indicador. */
    @PutMapping("/{metCod}")
    public MetaDTO editar(@PathVariable Long indCod, @PathVariable Long metCod,
                          @Valid @RequestBody MetaDTO dto) {
        return service.editar(indCod, metCod, dto);
    }

    /** Remove um período do indicador. */
    @DeleteMapping("/{metCod}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long indCod, @PathVariable Long metCod) {
        service.remover(indCod, metCod);
    }
}
