package com.github.davidpotentini.cerne2.controllers.pessoas;

import com.github.davidpotentini.cerne2.dto.pessoas.PessoasDTO;
import com.github.davidpotentini.cerne2.service.pessoas.PessoasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pessoas")
public class PessoasController {

    private final PessoasService pessoasService;

    public PessoasController(PessoasService pessoasService) {
        this.pessoasService = pessoasService;
    }

    @GetMapping
    public ResponseEntity<List<PessoasDTO>> findByIncubadora(){
        return ResponseEntity.ok(pessoasService.findByIncubadora());
    }

    @GetMapping("/{pessoaCod}")
    public ResponseEntity<PessoasDTO> findById(@PathVariable Long pessoaCod){
        return ResponseEntity.ok(pessoasService.findById(pessoaCod));
    }

    @GetMapping("/incubada/{incCod}")
    public ResponseEntity<List<PessoasDTO>> findByIncubada(@PathVariable Long incCod){
        return ResponseEntity.ok(pessoasService.findByIncubada(incCod));
    }

    @PostMapping
    public ResponseEntity<PessoasDTO> insert(@RequestBody PessoasDTO pessoasDTO){
        PessoasDTO pessoaSalva = pessoasService.save(pessoasDTO, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{pessoaCod}")
                .buildAndExpand(pessoaSalva.pessoaCod())
                .toUri();

        return ResponseEntity.created(location).body(pessoaSalva);
    }

    @PutMapping("/{pessoaCod}")
    public ResponseEntity<PessoasDTO> update (@RequestBody PessoasDTO pessoasDTO,
                                              @PathVariable Long pessoaCod){
        return ResponseEntity.ok(pessoasService.save(pessoasDTO, pessoaCod));
    }

    @DeleteMapping({"/{pessoaCod}"})
    public ResponseEntity<Void> delete(@PathVariable Long pessoaCod){
        pessoasService.delete(pessoaCod);

        return ResponseEntity.noContent().build();
    }
}
