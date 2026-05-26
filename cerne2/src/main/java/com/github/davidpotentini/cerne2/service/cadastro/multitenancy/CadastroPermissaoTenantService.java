package com.github.davidpotentini.cerne2.service.cadastro.multitenancy;

import com.github.davidpotentini.cerne2.enums.ERole;
import com.github.davidpotentini.cerne2.models.cadastro.UserPermissionsModel;
import com.github.davidpotentini.cerne2.repository.cadastro.RolesRepository;
import com.github.davidpotentini.cerne2.repository.cadastro.UserPermissionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Atribui um papel a um usuário gravando em USER_PERMISSIONS no schema do tenant.
 * Usa {@code REQUIRES_NEW}; o chamador deve definir o schema com
 * {@link com.github.davidpotentini.cerne2.configuration.multitenancy.TenantContext}
 * antes de invocar.
 */
@Service
public class CadastroPermissaoTenantService {

    private final RolesRepository rolesRepository;
    private final UserPermissionsRepository userPermissionsRepository;

    public CadastroPermissaoTenantService(RolesRepository rolesRepository,
                                          UserPermissionsRepository userPermissionsRepository) {
        this.rolesRepository = rolesRepository;
        this.userPermissionsRepository = userPermissionsRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void atribuirRole(Long usnCod, ERole role) {
        UserPermissionsModel userPermissionsModel = new UserPermissionsModel();
        userPermissionsModel.setUsnCod(usnCod);
        userPermissionsModel.setRolesModel(rolesRepository.findByRoleDescr(role.name())
                .orElseThrow(() -> new RuntimeException("Role não encontrada: " + role)));
        userPermissionsRepository.save(userPermissionsModel);
    }
}
