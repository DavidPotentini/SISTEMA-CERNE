package com.github.davidpotentini.cerne2.repository.cadastro;

import com.github.davidpotentini.cerne2.models.cadastro.UsersModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UsersModel, Long> {
}
