package com.github.davidpotentini.cerne2.repository.enderecos;

import com.github.davidpotentini.cerne2.models.enderecos.EnderecosModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<EnderecosModel, Long> {
}
