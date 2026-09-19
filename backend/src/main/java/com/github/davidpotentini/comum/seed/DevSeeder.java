package com.github.davidpotentini.comum.seed;

import com.github.davidpotentini.dto.incubadoras.IncubadoraDTO;
import com.github.davidpotentini.dto.login.AtivacaoContaDTO;
import com.github.davidpotentini.dto.usuarios.UsuarioConviteDTO;
import com.github.davidpotentini.enums.EStatusConta;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.incubadoras.IncubadorasModel;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.incubadoras.IncubadorasRepository;
import com.github.davidpotentini.service.incubadoras.IncubadoraService;
import com.github.davidpotentini.service.login.LoginService;
import com.github.davidpotentini.service.usuarios.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DevSeeder implements ApplicationRunner {

    private static final String NOME_INCUBADORA = "Espaço Empreendedor";
    private static final String EMAIL_INCUBADORA = "admin@espacoempreendedor.com";
    private static final String EMAIL_PLATAFORMA = "admin@cerne.local";
    private static final String SENHA = "cerne123";
    private static final Long PAPEL_PADRAO = 1L;

    private final boolean habilitado;
    private final IncubadoraService incubadoraService;
    private final UsuarioService usuarioService;
    private final LoginService loginService;
    private final ContasRepository contas;
    private final IncubadorasRepository incubadoras;
    private final PasswordEncoder passwordEncoder;

    public DevSeeder(@Value("${app.seed.enabled:false}") boolean habilitado,
                     IncubadoraService incubadoraService,
                     UsuarioService usuarioService,
                     LoginService loginService,
                     ContasRepository contas,
                     IncubadorasRepository incubadoras,
                     PasswordEncoder passwordEncoder) {
        this.habilitado = habilitado;
        this.incubadoraService = incubadoraService;
        this.usuarioService = usuarioService;
        this.loginService = loginService;
        this.contas = contas;
        this.incubadoras = incubadoras;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }
        seedAdminPlataforma();
        seedIncubadora();
    }

    private void seedAdminPlataforma() {
        if (contas.existsByEmail(EMAIL_PLATAFORMA)) {
            return;
        }
        ContasModel admin = new ContasModel();
        admin.setNome("Administrador da plataforma");
        admin.setEmail(EMAIL_PLATAFORMA);
        admin.setSenha(passwordEncoder.encode(SENHA));
        admin.setStatus(EStatusConta.ATIVO);
        admin.setAdminPlataforma(true);
        contas.save(admin);
    }

    private void seedIncubadora() {
        Long incCod = incubadoras.findFirstByNome(NOME_INCUBADORA)
                .map(IncubadorasModel::getIncCod)
                .orElseGet(() -> incubadoraService.salvar(
                        new IncubadoraDTO(null, NOME_INCUBADORA, null, null, null, null,
                                null, null, null, null, null, null, null, null), null).incCod());

        boolean inativa = incubadoras.findById(incCod)
                .filter(i -> i.getAtivadaEm() == null)
                .isPresent();
        if (inativa) {
            incubadoraService.alternarStatus(incCod);
        }

        if (!contas.existsByEmail(EMAIL_INCUBADORA)) {
            usuarioService.convidar(new UsuarioConviteDTO(
                    "Administrador", EMAIL_INCUBADORA, incCod, PAPEL_PADRAO));
            loginService.ativar(new AtivacaoContaDTO(EMAIL_INCUBADORA, SENHA));
        }
    }
}
