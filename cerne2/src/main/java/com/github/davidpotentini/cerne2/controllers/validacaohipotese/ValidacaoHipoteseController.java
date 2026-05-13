package com.github.davidpotentini.cerne2.controllers.validacaohipotese;

import com.github.davidpotentini.cerne2.dto.validacaohipotese.HipoteseDTO;
import com.github.davidpotentini.cerne2.dto.validacaohipotese.QuadroValidacaoHipoteseDTO;
import com.github.davidpotentini.cerne2.models.validacaohipotese.HipoteseModel;
import com.github.davidpotentini.cerne2.service.validacaohipotese.ValidacaoHipoteseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping(value = "{incCod}/validacaoHipotese")
public class ValidacaoHipoteseController {

    private ValidacaoHipoteseService validacaoHipoteseService;

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
        QuadroValidacaoHipoteseDTO quadroValidacaoHipoteseDTO2 = validacaoHipoteseService.saveQuadroValidacaoHipotese(quadroValidacaoHipoteseDTO, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{qvhCod}")
                .buildAndExpand(quadroValidacaoHipoteseDTO2.qvhCod())
                .toUri();

        return ResponseEntity.created(location).body(quadroValidacaoHipoteseDTO2);
    }

    @PutMapping("/qvhCod")
    public ResponseEntity<QuadroValidacaoHipoteseDTO> updateQuadroValidacaoHipotese(@RequestBody QuadroValidacaoHipoteseDTO quadroValidacaoHipoteseDTO, @PathVariable Long qvhCod){
        return ResponseEntity.ok(validacaoHipoteseService.saveQuadroValidacaoHipotese(quadroValidacaoHipoteseDTO, qvhCod));
    }

    public ResponseEntity<Void> deleteQuadroValidacaoHipotese(@PathVariable Long qvhCod){
        validacaoHipoteseService.deleteQuadroValidacaoHipotese(qvhCod);

        return ResponseEntity.noContent().build();
    }


    /*HIPOTESE*/
    @DeleteMapping("/{qvhCod}/indicadores/{hipCod}")
    public ResponseEntity<Void> deleteHipotese(@PathVariable Long hipCod){
        validacaoHipoteseService.deleteHipotese(hipCod);

        return ResponseEntity.noContent().build();
    }

}
