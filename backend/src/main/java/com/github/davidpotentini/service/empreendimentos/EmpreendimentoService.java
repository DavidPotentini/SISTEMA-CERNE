package com.github.davidpotentini.service.empreendimentos;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.empreendimentos.EmpreendimentoDTO;
import com.github.davidpotentini.dto.empreendimentos.PessoaEmpreendimentoDTO;
import com.github.davidpotentini.mapper.empreendimentos.EmpreendimentoMapper;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.model.empreendimentos.PessoaEmpreendimentoModel;
import com.github.davidpotentini.repository.empreendimentos.EmpreendimentosRepository;
import com.github.davidpotentini.repository.empreendimentos.PessoaEmpreendimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Empreendimentos da incubadora do usuário logado. Roda no schema do próprio tenant (o JWT já
 * deixou o {@code TenantContext} ativo), então {@code EMPREENDIMENTOS}/{@code PESSOAS_EMPREENDIMENTO}
 * são lidos e gravados direto — sem {@code callWithin}. O contato principal entre os membros da
 * startup é a pessoa {@code REPRESENTANTE_LEGAL}.
 */
@Service
public class EmpreendimentoService {

    private final EmpreendimentosRepository empreendimentos;
    private final PessoaEmpreendimentoRepository pessoasEmp;
    private final EmpreendimentoMapper mapper;

    public EmpreendimentoService(EmpreendimentosRepository empreendimentos,
                                 PessoaEmpreendimentoRepository pessoasEmp,
                                 EmpreendimentoMapper mapper) {
        this.empreendimentos = empreendimentos;
        this.pessoasEmp = pessoasEmp;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<EmpreendimentoDTO> listar() {
        List<EmpreendimentoDTO> resumos = new ArrayList<>();
        for (EmpreendimentosModel emp : empreendimentos.findAllByOrderByNomeAsc()) {
            resumos.add(mapper.toDTO(emp));
        }
        return resumos;
    }

    @Transactional(rollbackFor = Exception.class)
    public EmpreendimentoDTO criar(EmpreendimentoDTO dto) {
        EmpreendimentosModel emp = mapper.toModel(dto);
        empreendimentos.save(emp);
        // Pessoas iniciais da startup (opcionais) gravadas no mesmo fluxo de criação.
        if (dto.pessoas() != null) {
            for (PessoaEmpreendimentoDTO pessoaDto : dto.pessoas()) {
                PessoaEmpreendimentoModel pessoa = mapper.toModel(pessoaDto);
                pessoa.setEmpCod(emp.getEmpCod());
                pessoasEmp.save(pessoa);
            }
        }
        return mapper.toDTO(emp);
    }

    @Transactional(rollbackFor = Exception.class)
    public EmpreendimentoDTO editar(Long empCod, EmpreendimentoDTO dto) {
        EmpreendimentosModel emp = buscar(empCod);
        mapper.atualizar(dto, emp);
        empreendimentos.save(emp);
        return mapper.toDTO(emp);
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

    /** Marca a pessoa como representante legal do empreendimento; no máximo uma por empreendimento. */
    @Transactional(rollbackFor = Exception.class)
    public PessoaEmpreendimentoDTO definirRepresentanteLegal(Long empCod, Long pseCod) {
        PessoaEmpreendimentoModel pessoa = pessoasEmp.findById(pseCod)
                .orElseThrow(() -> new NaoEncontradoException("Pessoa do empreendimento", pseCod));
        if (!pessoa.getEmpCod().equals(empCod)) {
            throw new RegraNegocioException("A pessoa não pertence a este empreendimento.");
        }
        // Zera o representante atual (se for outro) antes de marcar o novo.
        pessoasEmp.findByEmpCodAndRepresentanteLegalTrue(empCod).ifPresent(atual -> {
            if (!atual.getPseCod().equals(pseCod)) {
                atual.setRepresentanteLegal(false);
                pessoasEmp.save(atual);
            }
        });
        pessoa.setRepresentanteLegal(true);
        pessoasEmp.save(pessoa);
        return mapper.toDTO(pessoa);
    }

    // ---- apoio ----

    private EmpreendimentosModel buscar(Long empCod) {
        return empreendimentos.findById(empCod)
                .orElseThrow(() -> new NaoEncontradoException("Empreendimento", empCod));
    }
}
