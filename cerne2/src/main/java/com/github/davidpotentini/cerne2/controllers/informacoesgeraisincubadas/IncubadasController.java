package com.github.davidpotentini.cerne2.controllers.informacoesgeraisincubadas;

import com.github.davidpotentini.cerne2.dto.arquivo.ArquivoDTO;
import com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas.IncubadasDTO;
import com.github.davidpotentini.cerne2.service.arquivos.ArquivoService;
import com.github.davidpotentini.cerne2.service.informacoesgeraisincubadas.IncubadasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/gerenciaIncubadas")
public class IncubadasController {

    private final IncubadasService incubadasService;
    private final ArquivoService arquivoService;

    public IncubadasController(IncubadasService incubadasService, ArquivoService arquivoService) {
        this.incubadasService = incubadasService;
        this.arquivoService = arquivoService;
    }

    @GetMapping
    public ResponseEntity<List<IncubadasDTO>> findAll(){
        return ResponseEntity.ok(incubadasService.findList());
    }

    @GetMapping("/{incCod}")
    public ResponseEntity<IncubadasDTO> findById(@PathVariable Long incCod){
        return ResponseEntity.ok(incubadasService.findById(incCod));
    }

    @PostMapping
    public ResponseEntity<IncubadasDTO> insert(@RequestBody IncubadasDTO incubadasDTO){
        IncubadasDTO incubadasDTOSalvo = incubadasService.save(incubadasDTO, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{incCod}")
                .buildAndExpand(incubadasDTOSalvo.incCod())
                .toUri();

        return ResponseEntity.created(location).body(incubadasDTOSalvo);
    }

    @PutMapping("/{incCod}")
    public ResponseEntity<IncubadasDTO> update(@RequestBody IncubadasDTO incubadasDTO,
                                               @PathVariable Long incCod){

        return ResponseEntity.ok(incubadasService.save(incubadasDTO, incCod));
    }

    @DeleteMapping("/{incCod}")
    public ResponseEntity<Void> delete(@PathVariable Long incCod){
        incubadasService.delete(incCod);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{incCod}/arquivos")
    public ResponseEntity<List<ArquivoDTO>> findArquivosIncubadas(@PathVariable Long incCod){
        return ResponseEntity.ok(incubadasService.findArquivosIncubadas(incCod));
    }

    @PostMapping("/{incCod}/arquivos")
    public ResponseEntity<ArquivoDTO> uploadArquivoIncubada(@PathVariable Long incCod,
                                                             MultipartFile multipartFile){

        ArquivoDTO arquivoDTO = incubadasService.uploadArquivosIncubadas(multipartFile, incCod);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{arqCod}")
                .buildAndExpand(arquivoDTO.arqCod())
                .toUri();

        return ResponseEntity.created(location).body(arquivoDTO);
    }

    @GetMapping("/{incCod}/arquivos/{arqCod}")
    public ResponseEntity<String> downloadArquivoIncubada(@PathVariable Long arqCod){
        return ResponseEntity.ok(arquivoService.gerarUrlDownload(arqCod));
    }

    @DeleteMapping("/{incCod}/arquivos/{arqCod}")
    public ResponseEntity<Void> deleteArquivoIncubada(@PathVariable Long arqCod){
        arquivoService.deletar(arqCod);

        return ResponseEntity.noContent().build();
    }
}
