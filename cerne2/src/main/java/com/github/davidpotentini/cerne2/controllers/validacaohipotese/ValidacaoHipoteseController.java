package com.github.davidpotentini.cerne2.controllers.validacaohipotese;

import com.github.davidpotentini.cerne2.dto.validacaohipotese.HipoteseDTO;
import com.github.davidpotentini.cerne2.dto.validacaohipotese.QuadroValidacaoHipoteseDTO;
import com.github.davidpotentini.cerne2.service.validacaohipotese.ValidacaoHipoteseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping(value = "/incubadas/{incCod}/validacaoHipotese")
public class ValidacaoHipoteseController {

    private final ValidacaoHipoteseService validacaoHipoteseService;

    public ValidacaoHipoteseController(ValidacaoHipoteseService validacaoHipoteseService) {
        this.validacaoHipoteseService = validacaoHipoteseService;
    }

    /*QUADRO VALIDACAO HIPOTESE*/

    @GetMapping
    public ResponseEntity<List<QuadroValidacaoHipoteseDTO>> findQuadroValidacaoHipoteseByIncCod(@PathVariable Long incCod){
        return ResponseEntity.ok(validacaoHipoteseService.findQuadroValidacaoHipoteseByIncCod(incCod));
    }

    @GetMapping("/{qvhCod}")
    public ResponseEntity<QuadroValidacaoHipoteseDTO> findQuadroValidacaoHipoteseById(@PathVariable Long qvhCod){
        return ResponseEntity.ok(validacaoHipoteseService.findQuadroValidacaoHipoteeById(qvhCod));
    }

    @PostMapping
    public ResponseEntity<QuadroValidacaoHipoteseDTO> insertQuadroValidacaoHipotese(@RequestBody QuadroValidacaoHipoteseDTO quadroValidacaoHipoteseDTO){
        QuadroValidacaoHipoteseDTO salvo = validacaoHipoteseService.saveQuadroValidacaoHipotese(quadroValidacaoHipoteseDTO, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{qvhCod}")
                .buildAndExpand(salvo.qvhCod())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{qvhCod}")
    public ResponseEntity<QuadroValidacaoHipoteseDTO> updateQuadroValidacaoHipotese(@RequestBody QuadroValidacaoHipoteseDTO quadroValidacaoHipoteseDTO, @PathVariable Long qvhCod){
        return ResponseEntity.ok(validacaoHipoteseService.saveQuadroValidacaoHipotese(quadroValidacaoHipoteseDTO, qvhCod));
    }

    @DeleteMapping("/{qvhCod}")
    public ResponseEntity<Void> deleteQuadroValidacaoHipotese(@PathVariable Long qvhCod){
        validacaoHipoteseService.deleteQuadroValidacaoHipotese(qvhCod);
        return ResponseEntity.noContent().build();
    }


    /*HIPOTESE*/

    @GetMapping("/{qvhCod}/hipoteses")
    public ResponseEntity<List<HipoteseDTO>> findHipoteseByQvhCod(@PathVariable Long qvhCod){
        return ResponseEntity.ok(validacaoHipoteseService.findHipoteseByQvhCod(qvhCod));
    }

    @PostMapping("/{qvhCod}/hipoteses")
    public ResponseEntity<HipoteseDTO> insertHipotese(@PathVariable Long qvhCod, @RequestBody HipoteseDTO hipoteseDTO){
        HipoteseDTO salva = validacaoHipoteseService.saveHipotese(hipoteseDTO, qvhCod, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{hipCod}")
                .buildAndExpand(salva.hipCod())
                .toUri();

        return ResponseEntity.created(location).body(salva);
    }

    @PutMapping("/{qvhCod}/hipoteses/{hipCod}")
    public ResponseEntity<HipoteseDTO> updateHipotese(@PathVariable Long qvhCod, @PathVariable Long hipCod, @RequestBody HipoteseDTO hipoteseDTO){
        return ResponseEntity.ok(validacaoHipoteseService.saveHipotese(hipoteseDTO, qvhCod, hipCod));
    }

    @DeleteMapping("/{qvhCod}/hipoteses/{hipCod}")
    public ResponseEntity<Void> deleteHipotese(@PathVariable Long hipCod){
        validacaoHipoteseService.deleteHipotese(hipCod);
        return ResponseEntity.noContent().build();
    }
}
