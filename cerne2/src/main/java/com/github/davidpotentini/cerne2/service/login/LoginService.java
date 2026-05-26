package com.github.davidpotentini.cerne2.service.login;

import com.github.davidpotentini.cerne2.configuration.multitenancy.TenantContext;
import com.github.davidpotentini.cerne2.configuration.security.JwtService;
import com.github.davidpotentini.cerne2.dto.login.EmpresaDTO;
import com.github.davidpotentini.cerne2.dto.login.LoginDTO;
import com.github.davidpotentini.cerne2.dto.login.LoginRespostaDTO;
import com.github.davidpotentini.cerne2.enums.ERole;
import com.github.davidpotentini.cerne2.models.cadastro.TenantsModel;
import com.github.davidpotentini.cerne2.models.cadastro.UsersModel;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import com.github.davidpotentini.cerne2.repository.cadastro.TenantsRepository;
import com.github.davidpotentini.cerne2.repository.cadastro.UserRepository;
import com.github.davidpotentini.cerne2.service.cadastro.multitenancy.PessoaTenantService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LoginService {

    private final TenantsRepository tenantsRepository;
    private final UserRepository userRepository;
    private final PessoaTenantService pessoaTenantService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginService(TenantsRepository tenantsRepository,
                        UserRepository userRepository,
                        PessoaTenantService pessoaTenantService,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.tenantsRepository = tenantsRepository;
        this.userRepository = userRepository;
        this.pessoaTenantService = pessoaTenantService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Empresas (tenants) disponíveis para seleção na tela de login.
     */
    public List<EmpresaDTO> listarEmpresas() {
        return tenantsRepository.findAll().stream()
                .map(tenant -> new EmpresaDTO(tenant.getTntCod(), tenant.getNomeEmpresa()))
                .toList();
    }

    /**
     * Autentica o usuário na empresa selecionada. Além de validar as credenciais
     * em USERS (schema public), exige que o e-mail esteja cadastrado na tabela
     * PESSOAS do tenant — é a PESSOA que define o tipo de empreendimento
     * (INCUBADORA/INCUBADA), o papel e, para INCUBADA, o INC_COD.
     */
    public LoginRespostaDTO login(LoginDTO loginDTO) {

        /*TENANTS*/

        TenantsModel tenant = tenantsRepository.findById(loginDTO.tntCod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));

        UsersModel usuario = userRepository.findByEmail(loginDTO.email())
                .filter(u -> passwordEncoder.matches(loginDTO.senha(), u.getSenha()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos"));

        if (usuario.getTenantsModel() == null
                || !usuario.getTenantsModel().getTntCod().equals(tenant.getTntCod())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Usuário não pertence à empresa selecionada");
        }

        PessoasModel pessoa = TenantContext.callWithin(tenant.getNomeSchema(),
                        () -> pessoaTenantService.buscarPorEmail(loginDTO.email()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "E-mail não cadastrado. Contate o administrador da empresa."));

        Long incCod = pessoa.getIncubadasModel() != null
                ? pessoa.getIncubadasModel().getIncCod()
                : null;

        /*SPRING SECURITY*/
        ERole role = ERole.fromIncCod(incCod);
        String token = jwtService.gerar(usuario, tenant, pessoa, role);

        return new LoginRespostaDTO(
                usuario.getUsnCod(),
                usuario.getEmail(),
                pessoa.getPessoaCod(),
                pessoa.getNome(),
                tenant.getTntCod(),
                tenant.getNomeEmpresa(),
                tenant.getNomeSchema(),
                pessoa.getTipoEmpreendimento(),
                role,
                incCod,
                token);
    }
}
