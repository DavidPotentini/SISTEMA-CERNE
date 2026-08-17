package com.github.davidpotentini.service.multitenancy;

import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Operações sobre PESSOAS de um schema de tenant específico, usadas <b>fora</b> do
 * contexto de uma requisição com header {@code X-Tenant} (cadastro e login). Os
 * métodos usam {@code REQUIRES_NEW} para abrir uma sessão Hibernate isolada; o chamador
 * deve definir o schema com {@code TenantContext.callWithin/runWithin} antes de invocar.
 *
 * <p><b>Mudança em relação ao antigo:</b> a identidade (nome/e-mail/senha) migrou para
 * {@code public.CONTAS}. No tenant a pessoa é só o vínculo local — por isso os métodos
 * operam por {@code ctaCod} (ref. fraca), não mais por e-mail.
 */
@Service
public class PessoaTenantService {

    private final PessoasRepository pessoasRepository;

    public PessoaTenantService(PessoasRepository pessoasRepository) {
        this.pessoasRepository = pessoasRepository;
    }

    /** Cria o vínculo local da conta global {@code ctaCod} nesta incubadora, com um papel. */
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
