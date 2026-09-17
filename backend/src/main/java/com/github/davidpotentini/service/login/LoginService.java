package com.github.davidpotentini.service.login;
import com.github.davidpotentini.dto.login.AtivacaoContaDTO;
import com.github.davidpotentini.dto.login.LoginResponse;
import com.github.davidpotentini.dto.login.LoginRequest;

import com.github.davidpotentini.enums.EStatusConta;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.comum.erro.AcessoNegadoException;
import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.comum.tenant.TenantContext;
import com.github.davidpotentini.configuration.security.JwtService;
import com.github.davidpotentini.comum.tenant.UsuarioAutenticado;
import com.github.davidpotentini.model.incubadoras.IncubadorasModel;
import com.github.davidpotentini.repository.incubadoras.IncubadorasRepository;
import com.github.davidpotentini.service.multitenancy.AutorizacaoTenantService;
import com.github.davidpotentini.service.multitenancy.AutorizacaoTenantService.PapelResolvido;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Autentica pela identidade global ({@code public.CONTAS}); a incubadora vem de {@code CONTAS.INC_COD}.
 * O administrador da plataforma recebe um token sem tenant (opera no {@code public}).
 */
@Service
public class LoginService {

    private final ContasRepository contasRepository;
    private final IncubadorasRepository incubadorasRepository;
    private final AutorizacaoTenantService autorizacaoTenantService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginService(ContasRepository contasRepository,
                        IncubadorasRepository incubadorasRepository,
                        AutorizacaoTenantService autorizacaoTenantService,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.contasRepository = contasRepository;
        this.incubadorasRepository = incubadorasRepository;
        this.autorizacaoTenantService = autorizacaoTenantService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest req) {
        ContasModel conta = contasRepository.findByEmail(req.email())
                .orElseThrow(LoginService::credenciaisInvalidas);

        if (!passwordEncoder.matches(req.senha(), conta.getSenha())) {
            throw credenciaisInvalidas();
        }

        // Administrador da plataforma: sem incubadora, token sem nomeSchema.
        if (conta.isAdminPlataforma()) {
            String token = jwtService.gerar(new UsuarioAutenticado(
                    conta.getCtaCod(), conta.getEmail(), null, null, null, true));
            return new LoginResponse(token, conta.getCtaCod(), conta.getNome(), conta.getEmail(),
                    null, true, null, null, Map.of());
        }

        // Usuário de incubadora: a gaveta vem de CONTAS.INC_COD (1:1).
        if (conta.getIncCod() == null) {
            throw new AcessoNegadoException("Conta sem incubadora vinculada.");
        }
        IncubadorasModel incubadora = incubadorasRepository.findById(conta.getIncCod())
                .orElseThrow(() -> new NaoEncontradoException("Incubadora", conta.getIncCod()));
        String nomeSchema = incubadora.getNomeSchema();

        // Entra na gaveta do tenant para resolver papel + permissões (REQUIRES_NEW dentro).
        PapelResolvido papel = TenantContext.callWithin(nomeSchema,
                () -> autorizacaoTenantService.resolver(conta.getCtaCod()));

        String token = jwtService.gerar(new UsuarioAutenticado(
                conta.getCtaCod(), conta.getEmail(), nomeSchema, papel.papCod(), papel.papelNome(), false));

        return new LoginResponse(token, conta.getCtaCod(), conta.getNome(), conta.getEmail(),
                nomeSchema, false, papel.papCod(), papel.papelNome(), papel.permissoes());
    }

    /** Mensagem única de erro para não revelar se o e-mail existe. */
    @Transactional
    public void ativar(AtivacaoContaDTO dto) {
        ContasModel conta = contasRepository.findByEmail(dto.email())
                .filter(c -> c.getStatus() == EStatusConta.CONVIDADO)
                .orElseThrow(() -> new RegraNegocioException(
                        "Conta não encontrada ou não está pendente de ativação."));
        conta.setSenha(passwordEncoder.encode(dto.senha()));
        conta.setStatus(EStatusConta.ATIVO);
        contasRepository.save(conta);
    }

    private static ResponseStatusException credenciaisInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
    }
}
