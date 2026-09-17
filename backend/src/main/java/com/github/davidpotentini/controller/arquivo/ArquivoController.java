package com.github.davidpotentini.controller.arquivo;

import com.github.davidpotentini.dto.arquivo.ArquivoDTO;
import com.github.davidpotentini.service.arquivo.ArquivoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/incubadora/arquivos")
public class ArquivoController {

    private final ArquivoService service;

    public ArquivoController(ArquivoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArquivoDTO upload(@RequestParam("arquivo") MultipartFile arquivo) {
        return service.upload(arquivo);
    }

    @GetMapping("/{arqCod}")
    public ArquivoDTO buscar(@PathVariable Long arqCod) {
        return service.buscar(arqCod);
    }
}
