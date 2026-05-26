package com.github.davidpotentini.cerne2.controllers.login;

import com.github.davidpotentini.cerne2.dto.login.EmpresaDTO;
import com.github.davidpotentini.cerne2.dto.login.LoginDTO;
import com.github.davidpotentini.cerne2.dto.login.LoginRespostaDTO;
import com.github.davidpotentini.cerne2.service.login.LoginService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/empresas")
    public ResponseEntity<List<EmpresaDTO>> listarEmpresas() {
        return ResponseEntity.ok(loginService.listarEmpresas());
    }

    @PostMapping
    public ResponseEntity<LoginRespostaDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(loginService.login(loginDTO));
    }
}
