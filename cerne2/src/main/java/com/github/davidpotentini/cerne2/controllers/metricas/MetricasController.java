package com.github.davidpotentini.cerne2.controllers.metricas;

import com.github.davidpotentini.cerne2.dto.metricas.IndicadoresMetricasDTO;
import com.github.davidpotentini.cerne2.dto.metricas.MetricasDTO;
import com.github.davidpotentini.cerne2.service.metricas.MetricasServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/metricas")
public class MetricasController {

    private final MetricasServices metricasServices;

    public MetricasController(MetricasServices metricasServices) {
        this.metricasServices = metricasServices;
    }

    @GetMapping
    public ResponseEntity<List<MetricasDTO>> findAll(){
        return ResponseEntity.ok(metricasServices.findAll());
    }

    @GetMapping("/{metCod}")
    public ResponseEntity<MetricasDTO> findByMetCod(@PathVariable Long metCod){
        return ResponseEntity.ok(metricasServices.findById(metCod));
    }

    @PostMapping
    public ResponseEntity<MetricasDTO> insert(@RequestBody MetricasDTO metricasDTO){
        MetricasDTO metricasDTOResponse = metricasServices.save(metricasDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{metCod}")
                .buildAndExpand(metricasDTOResponse.metCod())
                .toUri();

        return ResponseEntity.created(location).body(metricasDTOResponse);
    }

    @PutMapping("/{metCod}")
    public ResponseEntity<MetricasDTO> update(@RequestBody MetricasDTO metricasDTO){
        return ResponseEntity.ok(metricasServices.save(metricasDTO));
    }

    @DeleteMapping("/{metCod}")
    public ResponseEntity<Void> delete(@PathVariable Long metCod){
        metricasServices.delete(metCod);

        return ResponseEntity.noContent().build();
    }

    /*ADICIONAR NO FRONT*/
    @DeleteMapping("/{metCod}/indicadores/{indCod}")
    public ResponseEntity<Void> deleteIndicador(@PathVariable Long indCod){
        metricasServices.deleteIndicador(indCod);

        return ResponseEntity.noContent().build();
    }
}
