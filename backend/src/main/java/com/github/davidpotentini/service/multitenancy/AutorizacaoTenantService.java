package com.github.davidpotentini.service.multitenancy;

import com.github.davidpotentini.dto.usuarios.PapelResumoDTO;
import com.github.davidpotentini.enums.EAtivoInativo;
import com.github.davidpotentini.enums.ENivel;
import com.github.davidpotentini.enums.ERecurso;
import com.github.davidpotentini.model.papeis.PapeisModel;
import com.github.davidpotentini.repository.papeis.PapeisRepository;
import com.github.davidpotentini.model.papeis.PapelPermissaoModel;
import com.github.davidpotentini.repository.papeis.PapelPermissaoRepository;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Resolve, dentro do schema de um tenant, o papel e as permissões de uma conta. O chamador define o
 * schema com {@code TenantContext.callWithin(...)}; os métodos rodam em {@code REQUIRES_NEW}.
 */
@Service
public class AutorizacaoTenantService {

    private final PessoasRepository pessoasRepository;
    private final PapeisRepository papeisRepository;
    private final PapelPermissaoRepository papelPermissaoRepository;

    public AutorizacaoTenantService(PessoasRepository pessoasRepository,
                                    PapeisRepository papeisRepository,
                                    PapelPermissaoRepository papelPermissaoRepository) {
        this.pessoasRepository = pessoasRepository;
        this.papeisRepository = papeisRepository;
        this.papelPermissaoRepository = papelPermissaoRepository;
    }

    public record PapelResolvido(Long papCod, String papelNome, Map<ERecurso, ENivel> permissoes) {
        public static PapelResolvido vazio() {
            return new PapelResolvido(null, null, Map.of());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public PapelResolvido resolver(Long ctaCod) {
        PessoasModel pessoa = pessoasRepository.findByCtaCod(ctaCod).orElse(null);
        if (pessoa == null || pessoa.getPapCod() == null) {
            return PapelResolvido.vazio();
        }

        Long papCod = pessoa.getPapCod();
        String papelNome = papeisRepository.findById(papCod)
                .map(PapeisModel::getNome)
                .orElse(null);

        Map<ERecurso, ENivel> permissoes = papelPermissaoRepository.findByPapCod(papCod).stream()
                .collect(Collectors.toMap(PapelPermissaoModel::getRecurso, PapelPermissaoModel::getNivel));

        return new PapelResolvido(papCod, papelNome, permissoes);
    }

    public record PapelPessoa(Long papCod, String nome) {
        public static PapelPessoa vazio() {
            return new PapelPessoa(null, null);
        }
    }

    /** Versão enxuta de {@link #resolver} para a listagem: sem a matriz de permissões. */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public PapelPessoa resolverPapelPessoa(Long ctaCod) {
        PessoasModel pessoa = pessoasRepository.findByCtaCod(ctaCod).orElse(null);
        if (pessoa == null || pessoa.getPapCod() == null) {
            return PapelPessoa.vazio();
        }
        String nome = papeisRepository.findById(pessoa.getPapCod())
                .map(PapeisModel::getNome)
                .orElse(null);
        return new PapelPessoa(pessoa.getPapCod(), nome);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<PapelResumoDTO> listarPapeis() {
        return papeisRepository.findAll().stream()
                .filter(p -> p.getSituacao() == EAtivoInativo.ATIVO)
                .sorted(Comparator.comparing(PapeisModel::getNome))
                .map(p -> new PapelResumoDTO(p.getPapCod(), p.getNome()))
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void vincularPapel(Long ctaCod, Long papCod) {
        PessoasModel pessoa = pessoasRepository.findByCtaCod(ctaCod).orElseGet(PessoasModel::new);
        pessoa.setCtaCod(ctaCod);
        pessoa.setPapCod(papCod);
        pessoasRepository.save(pessoa);
    }
}
