package com.github.davidpotentini.controller.indicador;

import com.github.davidpotentini.comum.ciclo.EscopoCiclo;
import com.github.davidpotentini.dto.indicador.ApuracaoIndicadorDTO;
import com.github.davidpotentini.dto.indicador.PainelIndicadorDTO;
import com.github.davidpotentini.dto.indicador.PeriodoApuracaoDTO;
import com.github.davidpotentini.dto.indicador.ResultadoEntradaDTO;
import com.github.davidpotentini.service.indicador.ApuracaoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Apuração de indicadores do ciclo ativo (tenant vem do JWT). Listagem com resumo apurados/total;
 * períodos de um indicador com o resultado; registro (upsert) do resultado de um período.
 */
@RestController
@EscopoCiclo
@RequestMapping("/incubadora/apuracao")
public class ApuracaoController {

    private final ApuracaoService service;

    public ApuracaoController(ApuracaoService service) {
        this.service = service;
    }

    /** Indicadores do ciclo ativo com o resumo de apuração (apurados/total). */
    @GetMapping
    public List<ApuracaoIndicadorDTO> listar() {
        return service.listar();
    }

    /** Painel do ciclo: uma linha por indicador com meta/resultado somados e os sinais de atingido/pendente. */
    @GetMapping("/painel")
    public List<PainelIndicadorDTO> painel() {
        return service.painel();
    }

    /** Períodos de um indicador com o resultado apurado (quando houver). */
    @GetMapping("/{indCod}/periodos")
    public List<PeriodoApuracaoDTO> periodos(@PathVariable Long indCod) {
        return service.periodos(indCod);
    }

    /** Registra (ou atualiza) o resultado de um período. */
    @PutMapping("/{indCod}/periodos/{metCod}")
    public PeriodoApuracaoDTO registrar(@PathVariable Long indCod, @PathVariable Long metCod,
                                        @Valid @RequestBody ResultadoEntradaDTO dto) {
        return service.registrar(indCod, metCod, dto.valor());
    }
}
