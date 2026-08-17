package com.github.davidpotentini.service.empreendimentos;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.empreendimentos.EmpreendimentoDTO;
import com.github.davidpotentini.dto.empreendimentos.PessoaEmpreendimentoDTO;
import com.github.davidpotentini.dto.empreendimentos.ResponsavelDTO;
import com.github.davidpotentini.mapper.empreendimentos.EmpreendimentoMapper;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.model.empreendimentos.PessoaEmpreendimentoModel;
import com.github.davidpotentini.model.pessoas.PessoasModel;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.empreendimentos.EmpreendimentosRepository;
import com.github.davidpotentini.repository.empreendimentos.PessoaEmpreendimentoRepository;
import com.github.davidpotentini.repository.pessoas.PessoasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Empreendimentos da incubadora do usuário logado. Roda no schema do próprio tenant (o JWT já
 * deixou o {@code TenantContext} ativo), então {@code EMPREENDIMENTOS}/{@code PESSOAS_EMPREENDIMENTO}
 * são lidos e gravados direto — sem {@code callWithin}. Dois papéis: o responsável interno
 * ({@code RESP_PES_COD} → {@code PESSOAS}, nome em {@code public.CONTAS}) e o contato principal
 * ({@code PRINCIPAL}) entre os membros da startup.
 */
@Service
public class EmpreendimentoService {

    private final EmpreendimentosRepository empreendimentos;
    private final PessoaEmpreendimentoRepository pessoasEmp;
    private final PessoasRepository pessoas;
    private final ContasRepository contas;
    private final EmpreendimentoMapper mapper;

    public EmpreendimentoService(EmpreendimentosRepository empreendimentos,
                                 PessoaEmpreendimentoRepository pessoasEmp,
                                 PessoasRepository pessoas,
                                 ContasRepository contas,
                                 EmpreendimentoMapper mapper) {
        this.empreendimentos = empreendimentos;
        this.pessoasEmp = pessoasEmp;
        this.pessoas = pessoas;
        this.contas = contas;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<EmpreendimentoDTO> listar() {
        List<EmpreendimentoDTO> resumos = new ArrayList<>();
        for (EmpreendimentosModel emp : empreendimentos.findAllByOrderByNomeAsc()) {
            resumos.add(mapper.toDTO(emp, nomeResponsavel(emp.getRespPesCod())));
        }
        return resumos;
    }

    /** Candidatos a responsável interno: a equipe da incubadora (pessoas com conta). */
    @Transactional(readOnly = true)
    public List<ResponsavelDTO> listarResponsaveis() {
        List<ResponsavelDTO> lista = new ArrayList<>();
        for (PessoasModel pessoa : pessoas.findAll()) {
            ContasModel conta = contas.findById(pessoa.getCtaCod()).orElse(null);
            if (conta == null) {
                continue;
            }
            lista.add(new ResponsavelDTO(pessoa.getPesCod(), conta.getNome()));
        }
        return lista;
    }

    @Transactional(rollbackFor = Exception.class)
    public EmpreendimentoDTO criar(EmpreendimentoDTO dto) {
        EmpreendimentosModel emp = mapper.toModel(dto);
        empreendimentos.save(emp);
        return mapper.toDTO(emp, nomeResponsavel(emp.getRespPesCod()));
    }

    @Transactional(rollbackFor = Exception.class)
    public EmpreendimentoDTO editar(Long empCod, EmpreendimentoDTO dto) {
        EmpreendimentosModel emp = buscar(empCod);
        mapper.atualizar(dto, emp);
        empreendimentos.save(emp);
        return mapper.toDTO(emp, nomeResponsavel(emp.getRespPesCod()));
    }

    @Transactional(readOnly = true)
    public List<PessoaEmpreendimentoDTO> listarPessoas(Long empCod) {
        buscar(empCod); // valida a existência do empreendimento
        return mapper.toDTOList(pessoasEmp.findByEmpCodOrderByNomeAsc(empCod));
    }

    @Transactional(rollbackFor = Exception.class)
    public PessoaEmpreendimentoDTO adicionarPessoa(Long empCod, PessoaEmpreendimentoDTO dto) {
        buscar(empCod); // valida a existência do empreendimento
        PessoaEmpreendimentoModel pessoa = mapper.toModel(dto);
        pessoa.setEmpCod(empCod);
        pessoasEmp.save(pessoa);
        return mapper.toDTO(pessoa);
    }

    /** Marca a pessoa como contato principal do empreendimento; no máximo uma por empreendimento. */
    @Transactional(rollbackFor = Exception.class)
    public PessoaEmpreendimentoDTO definirPrincipal(Long empCod, Long pseCod) {
        PessoaEmpreendimentoModel pessoa = pessoasEmp.findById(pseCod)
                .orElseThrow(() -> new NaoEncontradoException("Pessoa do empreendimento", pseCod));
        if (!pessoa.getEmpCod().equals(empCod)) {
            throw new RegraNegocioException("A pessoa não pertence a este empreendimento.");
        }
        // Zera o principal atual (se for outro) antes de marcar o novo.
        pessoasEmp.findByEmpCodAndPrincipalTrue(empCod).ifPresent(atual -> {
            if (!atual.getPseCod().equals(pseCod)) {
                atual.setPrincipal(false);
                pessoasEmp.save(atual);
            }
        });
        pessoa.setPrincipal(true);
        pessoasEmp.save(pessoa);
        return mapper.toDTO(pessoa);
    }

    // ---- apoio ----

    private EmpreendimentosModel buscar(Long empCod) {
        return empreendimentos.findById(empCod)
                .orElseThrow(() -> new NaoEncontradoException("Empreendimento", empCod));
    }

    /** Nome do responsável interno (equipe → conta), ou {@code null} se não definido/inexistente. */
    private String nomeResponsavel(Long respPesCod) {
        if (respPesCod == null) {
            return null;
        }
        PessoasModel pessoa = pessoas.findById(respPesCod).orElse(null);
        if (pessoa == null) {
            return null;
        }
        return contas.findById(pessoa.getCtaCod()).map(ContasModel::getNome).orElse(null);
    }
}
