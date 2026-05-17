package com.github.davidpotentini.cerne2.controllers.servicosvaloragregado;

import com.github.davidpotentini.cerne2.dto.servicosvaloragregado.ServicosValorAgregadoDTO;
import com.github.davidpotentini.cerne2.service.servicosvaloragregado.ServicosValorAgregadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/servicosValorAgregado")
public class ServicosValorAgregadoController {

    private final ServicosValorAgregadoService servicosValorAgregadoService;

    public ServicosValorAgregadoController(ServicosValorAgregadoService servicosValorAgregadoService) {
        this.servicosValorAgregadoService = servicosValorAgregadoService;
    }

    @GetMapping
    public ResponseEntity<List<ServicosValorAgregadoDTO>> findList(){
        List<ServicosValorAgregadoDTO> servicosValorAgregadoDTO = servicosValorAgregadoService.findList();

        return ResponseEntity.ok(servicosValorAgregadoDTO);
    }

    @GetMapping("/{servCod}")
    public ResponseEntity<ServicosValorAgregadoDTO> findById(@PathVariable Long servCod){
        ServicosValorAgregadoDTO servicosValorAgregadoDTO = servicosValorAgregadoService.findById(servCod);

        return ResponseEntity.ok(servicosValorAgregadoDTO);
    }

    @PostMapping
    public ResponseEntity<ServicosValorAgregadoDTO> insert(@RequestBody ServicosValorAgregadoDTO servicosValorAgregadoDTO){
        ServicosValorAgregadoDTO servicosDTO = servicosValorAgregadoService.save(servicosValorAgregadoDTO, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{servCod}")
                .buildAndExpand(servicosDTO.servCod())
                .toUri();

        return ResponseEntity.created(location).body(servicosDTO);
    }

    @PutMapping("/{servCod}")
    public ResponseEntity<ServicosValorAgregadoDTO> update(@RequestBody ServicosValorAgregadoDTO servicosValorAgregadoDTO,
                                                           @PathVariable Long servCod) {

        ServicosValorAgregadoDTO servicosDTO = servicosValorAgregadoService.save(servicosValorAgregadoDTO, servCod);

        return ResponseEntity.ok(servicosDTO);
    }

    @DeleteMapping("/{servCod}")
    public ResponseEntity<Void> delete(@PathVariable Long servCod){
        servicosValorAgregadoService.delete(servCod);

        return ResponseEntity.noContent().build();
    }

}
