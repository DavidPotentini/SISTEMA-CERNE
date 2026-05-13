package com.github.davidpotentini.cerne2.service.cadastro.multitenancy;

import com.github.davidpotentini.cerne2.configuration.multitenancy.TenantContext;
import com.github.davidpotentini.cerne2.models.cadastro.UserPermissionsModel;
import com.github.davidpotentini.cerne2.repository.cadastro.RolesRepository;
import com.github.davidpotentini.cerne2.repository.cadastro.UserPermissionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    public void atribuirRole(Long usnCod, Long roleCod, String nomeSchema) {
        String anterior = TenantContext.get();
        TenantContext.set(nomeSchema);
        try {
            UserPermissionsModel userPermissionsModel = new UserPermissionsModel();
            userPermissionsModel.setUsnCod(usnCod);
            userPermissionsModel.setRolesModel(rolesRepository.findById(roleCod)
                    .orElseThrow(() -> new RuntimeException("Role não encontrada: " + roleCod)));
            userPermissionsRepository.save(userPermissionsModel);
        } finally {
            if (anterior == null) {
                TenantContext.clear();
            } else {
                TenantContext.set(anterior);
            }
        }
    }
}
