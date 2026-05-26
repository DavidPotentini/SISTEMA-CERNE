package com.github.davidpotentini.cerne2.repository.cadastro;

import com.github.davidpotentini.cerne2.models.cadastro.RolesModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<RolesModel, Long> {

    Optional<RolesModel> findByRoleDescr(String roleDescr);
}
