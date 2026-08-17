package com.github.davidpotentini.service.multitenancy;

import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Atribui um papel a uma pessoa no schema do tenant. Usa {@code REQUIRES_NEW}; o chamador
 * deve definir o schema com {@code TenantContext} antes de invocar.
 *
 * <p><b>Mudança em relação ao antigo:</b> não há mais tabela de junção
 * {@code USER_PERMISSIONS} nem {@code ERole}. O papel é uma coluna em {@code PESSOAS}
 * ({@code PAP_COD} → {@code PAPEIS}); atribuir papel = atualizar essa coluna. Quem cria
 * papéis {@code PADRAO}/{@code PERSONALIZADO} é o CRUD de PAPEIS; aqui só se vincula.
 */
@Service
public class CadastroPermissaoTenantService {

    private final PessoasRepository pessoasRepository;

    public CadastroPermissaoTenantService(PessoasRepository pessoasRepository) {
        this.pessoasRepository = pessoasRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void atribuirPapel(Long ctaCod, Long papCod) {
        PessoasModel pessoa = pessoasRepository.findByCtaCod(ctaCod)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pessoa não encontrada neste tenant para a conta: " + ctaCod));
        pessoa.setPapCod(papCod);
        pessoasRepository.save(pessoa);
    }
}
