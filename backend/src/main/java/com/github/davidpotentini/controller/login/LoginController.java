package com.github.davidpotentini.controller.login;
import com.github.davidpotentini.dto.login.AtivacaoContaDTO;
import com.github.davidpotentini.dto.login.LoginResponse;
import com.github.davidpotentini.dto.login.LoginRequest;
import com.github.davidpotentini.service.login.LoginService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return loginService.login(req);
    }

    @PostMapping("/ativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativar(@RequestBody @Valid AtivacaoContaDTO dto) {
        loginService.ativar(dto);
    }
}
