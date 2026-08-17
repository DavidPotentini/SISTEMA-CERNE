package com.github.davidpotentini.service.equipe;

import com.github.davidpotentini.dto.equipe.PessoaEquipeDTO;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.papeis.PapeisModel;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.papeis.PapeisRepository;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Equipe vinculada da incubadora do usuário logado. Roda no schema do próprio tenant
 * (o JWT já deixou o {@code TenantContext} ativo), então {@code PESSOAS}/{@code PAPEIS}
 * são lidos direto — sem {@code callWithin}. Nome/e-mail/situação vêm de {@code public.CONTAS}
 * pelo fallback do {@code search_path}.
 */
@Service
public class EquipeService {

    private final PessoasRepository pessoas;
    private final PapeisRepository papeis;
    private final ContasRepository contas;

    public EquipeService(PessoasRepository pessoas,
                         PapeisRepository papeis,
                         ContasRepository contas) {
        this.pessoas = pessoas;
        this.papeis = papeis;
        this.contas = contas;
    }

    @Transactional(readOnly = true)
    public List<PessoaEquipeDTO> listar() {
        List<PessoaEquipeDTO> equipe = new ArrayList<>();
        for (PessoasModel pessoa : pessoas.findAll()) {
            ContasModel conta = contas.findById(pessoa.getCtaCod()).orElse(null);
            if (conta == null) {
                continue;
            }
            equipe.add(new PessoaEquipeDTO(
                    conta.getCtaCod(),
                    conta.getNome(),
                    conta.getEmail(),
                    nomePapel(pessoa.getPapCod()),
                    conta.getStatus()));
        }
        return equipe;
    }

    /** Nome do papel local; {@code null} se a pessoa não tem papel ou ele não existe mais. */
    private String nomePapel(Long papCod) {
        if (papCod == null) {
            return null;
        }
        PapeisModel papel = papeis.findById(papCod).orElse(null);
        return papel == null ? null : papel.getNome();
    }
}
