package com.github.davidpotentini.cerne2.repository.cadastro;

import com.github.davidpotentini.cerne2.models.cadastro.TenantsModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantsRepository extends JpaRepository<TenantsModel, Long> {
}
