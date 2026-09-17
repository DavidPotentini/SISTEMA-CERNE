package com.github.davidpotentini.controller.ciclos;

import com.github.davidpotentini.comum.ciclo.EscopoCiclo;
import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.dto.ciclos.GerarCicloOpcoesDTO;
import com.github.davidpotentini.dto.ciclos.MaterializarCicloDTO;
import com.github.davidpotentini.service.ciclos.MaterializacaoMetodologiaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EscopoCiclo
@RequestMapping("/incubadora/materializacao-metodologia")
public class MaterializacaoMetodologiaController {

    private final MaterializacaoMetodologiaService service;

    public MaterializacaoMetodologiaController(MaterializacaoMetodologiaService service) {
        this.service = service;
    }

    @GetMapping("/alvo")
    public CicloDTO alvo() {
        return service.alvo();
    }

    @GetMapping("/empreendimentos")
    public GerarCicloOpcoesDTO opcoesGerar() {
        return service.opcoesGerar();
    }

    @PostMapping
    public CicloDTO materializarMetodologia(@RequestBody(required = false) MaterializarCicloDTO dto) {
        return service.materializarMetodologia(dto == null ? null : dto.empCods());
    }
}
