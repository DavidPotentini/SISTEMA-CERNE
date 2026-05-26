package com.github.davidpotentini.cerne2.controllers.cadastro;

import com.github.davidpotentini.cerne2.dto.cadastro.CadastroUsuarioDTO;
import com.github.davidpotentini.cerne2.service.cadastro.CadastroUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/cadastro")
public class CadastroUsuarioController {

    private final CadastroUsuarioService cadastroUsuarioService;

    public CadastroUsuarioController(CadastroUsuarioService cadastroUsuarioService){
        this.cadastroUsuarioService = cadastroUsuarioService;
    }

    @PostMapping
    private ResponseEntity cadastrarUsuario(@RequestBody CadastroUsuarioDTO cadastroUsuarioDTO){

        cadastroUsuarioService.cadastrarUsuario(cadastroUsuarioDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/login")
                .build()
                .toUri();

        /*ADICIONAR MENSAGEM DE ERRO*/

        return ResponseEntity.created(location).build();
    }
}
