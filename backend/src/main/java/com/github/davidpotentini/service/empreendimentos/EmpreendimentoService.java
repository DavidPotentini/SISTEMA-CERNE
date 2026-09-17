package com.github.davidpotentini.service.empreendimentos;

import com.github.davidpotentini.comum.ciclo.CicloContexto;
import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.ciclos.CicloDTO;
import com.github.davidpotentini.dto.empreendimentos.EmpreendimentoDTO;
import com.github.davidpotentini.dto.empreendimentos.PessoaEmpreendimentoDTO;
import com.github.davidpotentini.mapper.ciclos.CicloMapper;
import com.github.davidpotentini.mapper.empreendimentos.EmpreendimentoMapper;
import com.github.davidpotentini.model.ciclos.CicloEmpreendimentoModel;
import com.github.davidpotentini.model.ciclos.CiclosModel;
import com.github.davidpotentini.model.empreendimentos.EmpreendimentosModel;
import com.github.davidpotentini.model.empreendimentos.PessoaEmpreendimentoModel;
import com.github.davidpotentini.repository.ciclos.CicloEmpreendimentoRepository;
import com.github.davidpotentini.repository.ciclos.CiclosRepository;
import com.github.davidpotentini.repository.empreendimentos.EmpreendimentosRepository;
import com.github.davidpotentini.repository.empreendimentos.PessoaEmpreendimentoRepository;
import com.github.davidpotentini.service.planejamento.PlanejamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EmpreendimentoService {

    private final EmpreendimentosRepository empreendimentos;
    private final PessoaEmpreendimentoRepository pessoasEmp;
    private final CicloContexto cicloContexto;
    private final CicloEmpreendimentoRepository cicloEmpreendimentos;
    private final CiclosRepository ciclos;
    private final PlanejamentoService planejamento;
    private final EmpreendimentoMapper mapper;
    private final CicloMapper cicloMapper;

    public EmpreendimentoService(EmpreendimentosRepository empreendimentos,
                                 PessoaEmpreendimentoRepository pessoasEmp,
                                 CicloContexto cicloContexto,
                                 CicloEmpreendimentoRepository cicloEmpreendimentos,
                                 CiclosRepository ciclos,
                                 PlanejamentoService planejamento,
                                 EmpreendimentoMapper mapper,
                                 CicloMapper cicloMapper) {
        this.empreendimentos = empreendimentos;
        this.pessoasEmp = pessoasEmp;
        this.cicloContexto = cicloContexto;
        this.cicloEmpreendimentos = cicloEmpreendimentos;
        this.ciclos = ciclos;
        this.planejamento = planejamento;
        this.mapper = mapper;
        this.cicloMapper = cicloMapper;
    }

    @Transactional(readOnly = true)
    public List<CicloDTO> listarCiclos(Long empCod) {
        buscar(empCod);
        Set<Long> cicCods = new HashSet<>();
        for (CicloEmpreendimentoModel ce : cicloEmpreendimentos.findByEmpCod(empCod)) {
            cicCods.add(ce.getCicCod());
        }
        List<CicloDTO> resultado = new ArrayList<>();
        for (CiclosModel ciclo : ciclos.findAllByOrderByCicCodDesc()) {
            if (cicCods.contains(ciclo.getCicCod())) {
                resultado.add(cicloMapper.toDTO(ciclo));
            }
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<EmpreendimentoDTO> listar() {
        CiclosModel emFoco = cicloContexto.emFoco();
        Set<Long> membros = membrosDoCiclo(emFoco);
        List<EmpreendimentoDTO> resumos = new ArrayList<>();
        for (EmpreendimentosModel emp : empreendimentos.findAllByOrderByNomeAsc()) {
            Long cicCod = membros.contains(emp.getEmpCod()) ? emFoco.getCicCod() : null;
            resumos.add(mapper.toDTO(emp, cicCod));
        }
        return resumos;
    }

    @Transactional(readOnly = true)
    public List<EmpreendimentoDTO> listarDoCiclo() {
        CiclosModel emFoco = cicloContexto.emFoco();
        if (emFoco == null) {
            return new ArrayList<>();
        }
        Set<Long> membros = membrosDoCiclo(emFoco);
        List<EmpreendimentoDTO> resumos = new ArrayList<>();
        for (EmpreendimentosModel emp : empreendimentos.findAllByOrderByNomeAsc()) {
            if (membros.contains(emp.getEmpCod())) {
                resumos.add(mapper.toDTO(emp, emFoco.getCicCod()));
            }
        }
        return resumos;
    }

    @Transactional(rollbackFor = Exception.class)
    public EmpreendimentoDTO criar(EmpreendimentoDTO dto) {
        EmpreendimentosModel emp = mapper.toModel(dto);
        empreendimentos.save(emp);
        if (dto.pessoas() != null) {
            for (PessoaEmpreendimentoDTO pessoaDto : dto.pessoas()) {
                PessoaEmpreendimentoModel pessoa = mapper.toModel(pessoaDto);
                pessoa.setEmpCod(emp.getEmpCod());
                pessoasEmp.save(pessoa);
            }
        }
        Long cicCod = reconciliarCiclo(emp.getEmpCod(), dto.cicCod());
        return mapper.toDTO(emp, cicCod);
    }

    @Transactional(rollbackFor = Exception.class)
    public EmpreendimentoDTO editar(Long empCod, EmpreendimentoDTO dto) {
        EmpreendimentosModel emp = buscar(empCod);
        mapper.atualizar(dto, emp);
        empreendimentos.save(emp);
        Long cicCod = reconciliarCiclo(empCod, dto.cicCod());
        return mapper.toDTO(emp, cicCod);
    }

    private Set<Long> membrosDoCiclo(CiclosModel emFoco) {
        Set<Long> membros = new HashSet<>();
        if (emFoco != null) {
            for (CicloEmpreendimentoModel ce : cicloEmpreendimentos.findByCicCod(emFoco.getCicCod())) {
                membros.add(ce.getEmpCod());
            }
        }
        return membros;
    }

    private Long reconciliarCiclo(Long empCod, Long cicCodDto) {
        CiclosModel emFoco = cicloContexto.emFoco();
        if (emFoco == null) {
            return null;
        }
        Long cicCod = emFoco.getCicCod();
        boolean participar = cicCodDto != null;
        boolean participa = cicloEmpreendimentos.existsByCicCodAndEmpCod(cicCod, empCod);
        if (participar && !participa) {
            CicloEmpreendimentoModel ce = new CicloEmpreendimentoModel();
            ce.setCicCod(cicCod);
            ce.setEmpCod(empCod);
            cicloEmpreendimentos.save(ce);
        } else if (!participar && participa) {
            if (planejamento.empreendimentoTemEdicoes(cicCod, empCod)) {
                throw new RegraNegocioException(
                        "O empreendimento tem atividades com edições no ciclo publicado e não pode ser desvinculado.");
            }
            cicloEmpreendimentos.deleteByCicCodAndEmpCod(cicCod, empCod);
        }
        return participar ? cicCod : null;
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

    private EmpreendimentosModel buscar(Long empCod) {
        return empreendimentos.findById(empCod)
                .orElseThrow(() -> new NaoEncontradoException("Empreendimento", empCod));
    }
}
