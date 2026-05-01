package com.github.davidpotentini.cerne2.service.cadastro;

import com.github.davidpotentini.cerne2.models.cadastro.TenantsModel;
import com.github.davidpotentini.cerne2.models.cadastro.UserPermissionsModel;
import com.github.davidpotentini.cerne2.models.cadastro.UsersModel;
import com.github.davidpotentini.cerne2.repository.cadastro.RolesRepository;
import com.github.davidpotentini.cerne2.repository.cadastro.TenantsRepository;
import com.github.davidpotentini.cerne2.repository.cadastro.UserPermissionsRepository;
import com.github.davidpotentini.cerne2.repository.cadastro.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CadastroUsuarioService {

    private final UserRepository userRepository;
    private final TenantsRepository tenantsRepository;
    private final UserPermissionsRepository userPermissionsRepository;
    private final RolesRepository rolesRepository;
    private final JdbcTemplate jdbcTemplate;

    public CadastroUsuarioService(
            UserRepository userRepository,
            TenantsRepository tenantsRepository,
            UserPermissionsRepository userPermissionsRepository,
            RolesRepository rolesRepository,
            JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.tenantsRepository = tenantsRepository;
        this.userPermissionsRepository = userPermissionsRepository;
        this.rolesRepository = rolesRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public void cadastrarUsuarioEmpresaExistente(
            UsersModel usersModel,
            Long tntCod) {

        //Salva o tenant e seta para usersModel
        usersModel.setTenantsModel(tenantsRepository.findById(tntCod).orElseThrow(() -> new RuntimeException("Tenant não encontrado")));

        userRepository.save(usersModel);

        //Atribui o role de OPERADOR_INCUBADORA para o usuario criado
        UserPermissionsModel userPermissionsModel = new UserPermissionsModel();

        userPermissionsModel.setUsnCod(usersModel.getUsnCod());

        userPermissionsModel.setRolesModel(rolesRepository.findById(2L).orElseThrow(() -> new RuntimeException("Usuário não encontrado")));

        userPermissionsRepository.save(userPermissionsModel);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cadastrarUsuarioEmpresaNaoExistente(
            UsersModel usersModel,
            TenantsModel tenantsModel) {

        //Salva o tenant e seta para usersModel
        TenantsModel tenant = tenantsRepository.save(tenantsModel);

        usersModel.setTenantsModel(tenant);

        userRepository.save(usersModel);

        //Usar anotações específicas de multitenancy ao inves disso
        jdbcTemplate.execute("SET search_path TO " + tenant.getNomeSchema());

        //Atribui o role de ADMIN_INCUBADORA para o usuario criado
        UserPermissionsModel userPermissionsModel = new UserPermissionsModel();

        userPermissionsModel.setUsnCod(usersModel.getUsnCod());

        userPermissionsModel.setRolesModel(rolesRepository.findById(1L).orElseThrow(() -> new RuntimeException("Usuário não encontrado")));

        userPermissionsRepository.save(userPermissionsModel);
    }
}
