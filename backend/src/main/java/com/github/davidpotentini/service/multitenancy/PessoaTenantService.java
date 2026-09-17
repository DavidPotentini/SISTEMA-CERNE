package com.github.davidpotentini.service.multitenancy;

import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Operações sobre PESSOAS de um tenant, usadas fora de uma requisição com {@code X-Tenant} (cadastro
 * e login). Os métodos usam {@code REQUIRES_NEW}; o chamador deve definir o schema com
 * {@code TenantContext.callWithin/runWithin} antes de invocar.
 */
@Service
public class PessoaTenantService {

    private final PessoasRepository pessoasRepository;

    public PessoaTenantService(PessoasRepository pessoasRepository) {
        this.pessoasRepository = pessoasRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public PessoasModel criarPessoa(Long ctaCod, Long papCod) {
        PessoasModel pessoa = new PessoasModel();
        pessoa.setCtaCod(ctaCod);
        pessoa.setPapCod(papCod);
        return pessoasRepository.save(pessoa);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Optional<PessoasModel> buscarPorCtaCod(Long ctaCod) {
        return pessoasRepository.findByCtaCod(ctaCod);
    }
}
