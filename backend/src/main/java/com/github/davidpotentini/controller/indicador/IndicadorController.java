package com.github.davidpotentini.controller.indicador;

import com.github.davidpotentini.comum.ciclo.EscopoCiclo;
import com.github.davidpotentini.dto.indicador.IndicadorCicloDTO;
import com.github.davidpotentini.dto.indicador.PraticaOpcaoDTO;
import com.github.davidpotentini.service.indicador.IndicadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
@EscopoCiclo
@RequestMapping("/incubadora/indicadores")
public class IndicadorController {

    private final IndicadorService service;

    public IndicadorController(IndicadorService service) {
        this.service = service;
    }

    @GetMapping
    public List<IndicadorCicloDTO> listar() {
        return service.listar();
    }

    @GetMapping("/vinculos")
    public List<PraticaOpcaoDTO> vinculos() {
        return service.vinculos();
    }

    @PostMapping("/complementares")
    @ResponseStatus(HttpStatus.CREATED)
    public IndicadorCicloDTO definirComplementar(@Valid @RequestBody IndicadorCicloDTO dto) {
        return service.definirComplementar(dto);
    }

    @PutMapping("/{indCod}")
    public IndicadorCicloDTO editar(@PathVariable Long indCod,
                                    @Valid @RequestBody IndicadorCicloDTO dto) {
        return service.editar(indCod, dto);
    }
}
