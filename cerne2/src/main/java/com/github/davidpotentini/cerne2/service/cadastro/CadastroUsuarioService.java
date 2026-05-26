package com.github.davidpotentini.cerne2.service.cadastro;

import com.github.davidpotentini.cerne2.configuration.multitenancy.TenantContext;
import com.github.davidpotentini.cerne2.dto.cadastro.CadastroUsuarioDTO;
import com.github.davidpotentini.cerne2.enums.ERole;
import com.github.davidpotentini.cerne2.mapper.cadastro.CadastroUsuarioMapper;
import com.github.davidpotentini.cerne2.models.cadastro.TenantsModel;
import com.github.davidpotentini.cerne2.models.cadastro.UsersModel;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import com.github.davidpotentini.cerne2.repository.cadastro.TenantsRepository;
import com.github.davidpotentini.cerne2.repository.cadastro.UserRepository;
import com.github.davidpotentini.cerne2.service.cadastro.multitenancy.CadastroPermissaoTenantService;
import com.github.davidpotentini.cerne2.service.cadastro.multitenancy.PessoaTenantService;
import com.github.davidpotentini.cerne2.service.cadastro.multitenancy.TenantSchemaProvisioner;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CadastroUsuarioService {

    private final UserRepository userRepository;
    private final TenantsRepository tenantsRepository;
    private final TenantSchemaProvisioner tenantSchemaProvisioner;
    private final CadastroPermissaoTenantService cadastroPermissaoTenantService;
    private final PessoaTenantService pessoaTenantService;
    private final CadastroUsuarioMapper cadastroUsuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public CadastroUsuarioService(UserRepository userRepository,
                                  TenantsRepository tenantsRepository,
                                  TenantSchemaProvisioner tenantSchemaProvisioner,
                                  CadastroPermissaoTenantService cadastroPermissaoTenantService,
                                  PessoaTenantService pessoaTenantService,
                                  CadastroUsuarioMapper cadastroUsuarioMapper,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tenantsRepository = tenantsRepository;
        this.tenantSchemaProvisioner = tenantSchemaProvisioner;
        this.cadastroPermissaoTenantService = cadastroPermissaoTenantService;
        this.pessoaTenantService = pessoaTenantService;
        this.cadastroUsuarioMapper = cadastroUsuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = Exception.class)
    public void cadastrarUsuario(CadastroUsuarioDTO cadastroUsuarioDTO) {
        if (cadastroUsuarioDTO.tntCod() != null) {
            cadastrarUsuarioEmpresaExistente(cadastroUsuarioDTO);
        } else {
            cadastrarUsuarioEmpresaNova(cadastroUsuarioDTO);
        }
    }

    /**
     * Cadastro de uma incubadora nova: cria o tenant, o schema, o usuário admin e
     * insere automaticamente a PESSOA correspondente (TIPO_EMPREENDIMENTO =
     * INCUBADORA) para que o admin consiga efetuar login.
     */
    private void cadastrarUsuarioEmpresaNova(CadastroUsuarioDTO cadastroUsuarioDTO) {
        TenantsModel tenant = tenantsRepository.save(cadastroUsuarioMapper.toTenantsModel(cadastroUsuarioDTO));

        UsersModel usuario = montarUsuario(cadastroUsuarioDTO, tenant);
        userRepository.save(usuario);

        tenantSchemaProvisioner.criarSchemaECarregarDDL(tenant.getNomeSchema());

        TenantContext.runWithin(tenant.getNomeSchema(), () -> {
            pessoaTenantService.criarPessoaIncubadora(cadastroUsuarioDTO);
            cadastroPermissaoTenantService.atribuirRole(usuario.getUsnCod(), ERole.ADMIN_INCUBADORA);
        });
    }

    /**
     * Cadastro em uma empresa já existente: o e-mail precisa estar previamente
     * cadastrado na tabela PESSOAS do tenant. O papel é resolvido pelo tipo de
     * empreendimento da pessoa (INCUBADORA ou INCUBADA).
     */
    private void cadastrarUsuarioEmpresaExistente(CadastroUsuarioDTO cadastroUsuarioDTO) {
        TenantsModel tenant = tenantsRepository.findById(cadastroUsuarioDTO.tntCod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));

        PessoasModel pessoa = TenantContext.callWithin(tenant.getNomeSchema(),
                        () -> pessoaTenantService.buscarPorEmail(cadastroUsuarioDTO.email()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "E-mail não cadastrado na tabela de pessoas. Contate o administrador da empresa."));

        UsersModel usuario = montarUsuario(cadastroUsuarioDTO, tenant);
        userRepository.save(usuario);

        Long incCod = pessoa.getIncubadasModel() != null
                ? pessoa.getIncubadasModel().getIncCod()
                : null;
        ERole role = ERole.fromIncCod(incCod);
        TenantContext.runWithin(tenant.getNomeSchema(),
                () -> cadastroPermissaoTenantService.atribuirRole(usuario.getUsnCod(), role));
    }

    private UsersModel montarUsuario(CadastroUsuarioDTO cadastroUsuarioDTO, TenantsModel tenant) {
        UsersModel usuario = cadastroUsuarioMapper.toUserModel(cadastroUsuarioDTO);
        usuario.setSenha(passwordEncoder.encode(cadastroUsuarioDTO.senha()));
        usuario.setTenantsModel(tenant);
        return usuario;
    }
}
