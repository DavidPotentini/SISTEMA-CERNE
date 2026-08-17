package com.github.davidpotentini.controller.usuarios;

import com.github.davidpotentini.comum.autorizacao.RequerAdmin;
import com.github.davidpotentini.dto.usuarios.PapelResumoDTO;
import com.github.davidpotentini.dto.usuarios.UsuarioConviteDTO;
import com.github.davidpotentini.dto.usuarios.UsuarioEdicaoDTO;
import com.github.davidpotentini.dto.usuarios.UsuarioResumoDTO;
import com.github.davidpotentini.service.usuarios.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Módulo "Usuários da plataforma" do administrador. Todas as rotas exigem admin. */
@RestController
@RequestMapping("/admin/usuarios")
@RequerAdmin
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    /** Listagem de todos os usuários da plataforma. */
    @GetMapping
    public List<UsuarioResumoDTO> listar() {
        return service.listar();
    }

    /** Papéis disponíveis para atribuição na incubadora informada (papéis do tenant). */
    @GetMapping("/papeis")
    public List<PapelResumoDTO> listarPapeis(@RequestParam Long incCod) {
        return service.listarPapeis(incCod);
    }

    /** Adicionar usuário: convite — nasce em "Convidado". */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResumoDTO convidar(@Valid @RequestBody UsuarioConviteDTO dto) {
        return service.convidar(dto);
    }

    /** Editar um usuário existente: nome, incubadora e papel. */
    @PutMapping("/{id}")
    public UsuarioResumoDTO editar(@PathVariable Long id,
                                   @Valid @RequestBody UsuarioEdicaoDTO dto) {
        return service.editar(id, dto);
    }

    /** Reativar/Suspender: alterna a situação da conta. */
    @PatchMapping("/{id}/status")
    public UsuarioResumoDTO alternarStatus(@PathVariable Long id) {
        return service.alternarStatus(id);
    }
}
