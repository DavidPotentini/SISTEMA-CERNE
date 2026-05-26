package com.github.davidpotentini.cerne2.service.cadastro.multitenancy;

import com.github.davidpotentini.cerne2.dto.cadastro.CadastroUsuarioDTO;
import com.github.davidpotentini.cerne2.mapper.cadastro.CadastroUsuarioMapper;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import com.github.davidpotentini.cerne2.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Operações sobre a tabela PESSOAS de um schema de tenant específico, usadas
 * fora do contexto de uma requisição com header {@code X-Tenant} (cadastro e
 * login). Os métodos usam {@code REQUIRES_NEW} para abrir uma sessão Hibernate
 * isolada; o chamador deve definir o schema com
 * {@link com.github.davidpotentini.cerne2.configuration.multitenancy.TenantContext}
 * antes de invocá-los.
 */
@Service
public class PessoaTenantService {

    private final PessoasRepository pessoasRepository;
    private final CadastroUsuarioMapper cadastroUsuarioMapper;

    public PessoaTenantService(PessoasRepository pessoasRepository,
                               CadastroUsuarioMapper cadastroUsuarioMapper) {
        this.pessoasRepository = pessoasRepository;
        this.cadastroUsuarioMapper = cadastroUsuarioMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public PessoasModel criarPessoaIncubadora(CadastroUsuarioDTO cadastroUsuarioDTO) {
        return pessoasRepository.save(cadastroUsuarioMapper.toPessoaModel(cadastroUsuarioDTO));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Optional<PessoasModel> buscarPorEmail(String email) {
        return pessoasRepository.findByEmail(email);
    }
}
