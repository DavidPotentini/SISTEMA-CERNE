package com.github.davidpotentini.cerne2.controllers.pessoas;

import com.github.davidpotentini.cerne2.dto.pessoas.request.PessoasDTORequest;
import com.github.davidpotentini.cerne2.dto.pessoas.response.PessoasDTOResponse;
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
    public ResponseEntity<List<PessoasDTOResponse>> findByIncubadora(){
        return ResponseEntity.ok(pessoasService.findByIncubadora());
    }

    @GetMapping("/{pessoaCod}")
    public ResponseEntity<PessoasDTOResponse> findById(@PathVariable Long pessoaCod){
        return ResponseEntity.ok(pessoasService.findById(pessoaCod));
    }

    @GetMapping("/incubada/{incCod}")
    public ResponseEntity<List<PessoasDTOResponse>> findByIncubada(@PathVariable Long incCod){
        return ResponseEntity.ok(pessoasService.findByIncubada(incCod));
    }

    @PostMapping
    public ResponseEntity<PessoasDTOResponse> insert(@RequestBody PessoasDTORequest pessoasDTORequest){
        PessoasDTOResponse pessoasDTOResponse = pessoasService.save(pessoasDTORequest, null);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{/{pessoaCod}")
                .buildAndExpand(pessoasDTOResponse.pessoaCod())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{pessoaCod}")
    public ResponseEntity<PessoasDTOResponse> update (@RequestBody PessoasDTORequest pessoasDTORequest,
                                                      @PathVariable Long pessoaCod){
        return ResponseEntity.ok(pessoasService.save(pessoasDTORequest, pessoaCod));
    }

    public ResponseEntity<Void> delete(@PathVariable Long pessoaCod){
        pessoasService.delete(pessoaCod);

        return ResponseEntity.noContent().build();
    }
}
