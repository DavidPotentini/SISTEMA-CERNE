package com.github.davidpotentini.cerne2.service.cadastro;

import com.github.davidpotentini.cerne2.models.cadastro.TenantsModel;
import com.github.davidpotentini.cerne2.models.cadastro.UsersModel;
import com.github.davidpotentini.cerne2.repository.cadastro.TenantsRepository;
import com.github.davidpotentini.cerne2.repository.cadastro.UserRepository;
import com.github.davidpotentini.cerne2.service.cadastro.multitenancy.CadastroPermissaoTenantService;
import com.github.davidpotentini.cerne2.service.cadastro.multitenancy.TenantSchemaProvisioner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroUsuarioService {

    private static final Long ROLE_ADMIN_INCUBADORA = 1L;
    private static final Long ROLE_OPERADOR_INCUBADORA = 2L;

    private final UserRepository userRepository;
    private final TenantsRepository tenantsRepository;
    private final TenantSchemaProvisioner tenantSchemaProvisioner;
    private final CadastroPermissaoTenantService cadastroPermissaoTenantService;

    public CadastroUsuarioService(UserRepository userRepository,
                                  TenantsRepository tenantsRepository,
                                  TenantSchemaProvisioner tenantSchemaProvisioner,
                                  CadastroPermissaoTenantService cadastroPermissaoTenantService) {
        this.userRepository = userRepository;
        this.tenantsRepository = tenantsRepository;
        this.tenantSchemaProvisioner = tenantSchemaProvisioner;
        this.cadastroPermissaoTenantService = cadastroPermissaoTenantService;
    }

    @Transactional(rollbackFor = Exception.class)
    public void cadastrarUsuarioIncubadoraExistente(UsersModel usersModel, Long tntCod) {
        TenantsModel tenant = tenantsRepository.findById(tntCod)
                .orElseThrow(() -> new RuntimeException("Tenant não encontrado"));

        usersModel.setTenantsModel(tenant);
        userRepository.save(usersModel);

        cadastroPermissaoTenantService.atribuirRole(
                usersModel.getUsnCod(), ROLE_OPERADOR_INCUBADORA, tenant.getNomeSchema());
    }

    @Transactional(rollbackFor = Exception.class)
    public void cadastrarUsuarioIncubadoraNaoExistente(UsersModel usersModel, TenantsModel tenantsModel) {
        TenantsModel tenant = tenantsRepository.save(tenantsModel);
        usersModel.setTenantsModel(tenant);
        userRepository.save(usersModel);

        tenantSchemaProvisioner.criarSchemaECarregarDDL(tenant.getNomeSchema());

        cadastroPermissaoTenantService.atribuirRole(
                usersModel.getUsnCod(), ROLE_ADMIN_INCUBADORA, tenant.getNomeSchema());
    }
}
