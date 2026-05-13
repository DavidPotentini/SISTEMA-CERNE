package com.github.davidpotentini.cerne2.controllers.informacoesgeraisincubadas;

import com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas.request.IncubadasDTORequest;
import com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas.respose.IncubadasDTOResponse;
import com.github.davidpotentini.cerne2.service.informacoesgeraisincubadas.IncubadasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/gerenciaIncubadas")
public class IncubadasController {

    private final IncubadasService incubadasService;

    public IncubadasController(IncubadasService incubadasService) {
        this.incubadasService = incubadasService;
    }

    @GetMapping
    public ResponseEntity<List<IncubadasDTOResponse>> findAll(){
        return ResponseEntity.ok(incubadasService.findList());
    }

    @GetMapping("/{incCod}")
    public ResponseEntity<IncubadasDTOResponse> findById(@PathVariable Long incCod){
        return ResponseEntity.ok(incubadasService.findById(incCod));
    }

    @PostMapping
    public ResponseEntity<IncubadasDTOResponse> insert(@RequestBody IncubadasDTORequest incubadasDTORequest){
        IncubadasDTOResponse incubadasDTOResponse = incubadasService.save(incubadasDTORequest, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{incCod}")
                .buildAndExpand(incubadasDTOResponse.incCod())
                .toUri();

        return ResponseEntity.created(location).body(incubadasDTOResponse);
    }

    @PutMapping("/{incCod}")
    public ResponseEntity<IncubadasDTOResponse> update(@RequestBody IncubadasDTORequest incubadasDTORequest,
                                                        @PathVariable Long incCod){

        return ResponseEntity.ok(incubadasService.save(incubadasDTORequest, incCod));
    }

    @DeleteMapping("/{incCod}")
    public ResponseEntity<Void> delete(@PathVariable Long incCod){
        incubadasService.delete(incCod);

        return ResponseEntity.noContent().build();
    }
}
