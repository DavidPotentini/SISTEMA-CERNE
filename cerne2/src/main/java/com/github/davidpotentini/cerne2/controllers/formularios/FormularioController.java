package com.github.davidpotentini.cerne2.controllers.formularios;

import com.github.davidpotentini.cerne2.dto.formulario.FormularioDTO;
import com.github.davidpotentini.cerne2.service.formulario.FormularioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/formularios")
public class FormularioController {

    private final FormularioService formularioService;

    public FormularioController(FormularioService formularioService) {
        this.formularioService = formularioService;
    }

    @GetMapping
    public ResponseEntity<List<FormularioDTO>> findAll(){
        return ResponseEntity.ok(formularioService.findAll());
    }

    @GetMapping("/{frmCod}")
    public ResponseEntity<FormularioDTO> findById(@PathVariable Long frmCod){
        return ResponseEntity.ok(formularioService.findById(frmCod));
    }

    @PostMapping
    public ResponseEntity<FormularioDTO> insert(@RequestBody FormularioDTO formularioDTO){
        FormularioDTO formularioDTO2 = formularioService.save(formularioDTO, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{frmCod}")
                .buildAndExpand(formularioDTO2.frmCod())
                .toUri();

        return ResponseEntity.created(location).body(formularioDTO2);
    }

    @PutMapping("/{frmCod}")
    public ResponseEntity<FormularioDTO> update(@RequestBody FormularioDTO formularioDTO,
                                                @PathVariable Long frmCod){
        return ResponseEntity.ok(formularioService.save(formularioDTO, frmCod));
    }

    @DeleteMapping("/{frmCod}")
    public ResponseEntity<Void> delete(@PathVariable Long frmCod){
        formularioService.delete(frmCod);

        return ResponseEntity.noContent().build();
    }
}
