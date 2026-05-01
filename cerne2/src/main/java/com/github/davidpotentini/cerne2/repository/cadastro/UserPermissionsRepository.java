package com.github.davidpotentini.cerne2.repository.cadastro;

import com.github.davidpotentini.cerne2.models.cadastro.UserPermissionsModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionsRepository extends JpaRepository <UserPermissionsModel, Long> {
}
