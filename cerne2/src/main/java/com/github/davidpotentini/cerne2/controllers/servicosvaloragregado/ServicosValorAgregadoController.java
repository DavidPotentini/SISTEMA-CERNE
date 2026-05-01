package com.github.davidpotentini.cerne2.controllers.servicosvaloragregado;

import com.github.davidpotentini.cerne2.dto.servicosvaloragregado.request.ServicosValorAgregadoDTORequest;
import com.github.davidpotentini.cerne2.dto.servicosvaloragregado.response.ServicosValorAgregadoDTOResponse;
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
    public ResponseEntity<List<ServicosValorAgregadoDTOResponse>> findList(){
        List<ServicosValorAgregadoDTOResponse> servicosValorAgregadoDTOResponse = servicosValorAgregadoService.findList();

        return ResponseEntity.ok(servicosValorAgregadoDTOResponse);
    }

    @GetMapping("/{servCod}")
    public ResponseEntity<ServicosValorAgregadoDTOResponse> findById(@PathVariable Long servCod){
        ServicosValorAgregadoDTOResponse servicosValorAgregadoDTOResponse = servicosValorAgregadoService.findById(servCod);

        return ResponseEntity.ok(servicosValorAgregadoDTOResponse);
    }

    @PostMapping
    public ResponseEntity<ServicosValorAgregadoDTOResponse> insert(@RequestBody ServicosValorAgregadoDTORequest servicosValorAgregadoDTORequest){
        ServicosValorAgregadoDTOResponse servicosDTO = servicosValorAgregadoService.save(servicosValorAgregadoDTORequest, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{servCod}")
                .buildAndExpand(servicosDTO.servCod())
                .toUri();

        return ResponseEntity.created(location).body(servicosDTO);
    }

    @PutMapping("/{servCod}")
    public ResponseEntity<ServicosValorAgregadoDTOResponse> update(@RequestBody ServicosValorAgregadoDTORequest servicosValorAgregadoDTORequest,
                                                                   @PathVariable Long servCod) {

        ServicosValorAgregadoDTOResponse servicosDTO = servicosValorAgregadoService.save(servicosValorAgregadoDTORequest, servCod);

        return ResponseEntity.ok(servicosDTO);
    }

    @DeleteMapping("/{servCod}")
    public ResponseEntity<Void> delete(@PathVariable Long servCod){
        servicosValorAgregadoService.delete(servCod);

        return ResponseEntity.noContent().build();
    }

}
