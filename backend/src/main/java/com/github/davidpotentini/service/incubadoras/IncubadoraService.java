package com.github.davidpotentini.service.incubadoras;

import com.github.davidpotentini.comum.erro.AcessoNegadoException;
import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.tenant.TenantSchemaProvisioner;
import com.github.davidpotentini.dto.incubadoras.IncubadoraDTO;
import com.github.davidpotentini.dto.incubadoras.IncubadoraResumoDTO;
import com.github.davidpotentini.enums.EStatusIncubadora;
import com.github.davidpotentini.mapper.incubadoras.IncubadoraMapper;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.incubadoras.IncubadorasModel;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.incubadoras.IncubadorasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Opera no schema {@code public} (INCUBADORAS,
 * CONTAS) — sem troca de tenant. É o agregado "incubadora": resolve o nome do responsável
 * e a contagem de usuários. A metodologia atual é sempre CERNE 1.
 *
 * <p>Criação: gera o {@code NOME_SCHEMA} e nasce em {@code AGUARDANDO_ATIVACAO}. O schema
 * do tenant só é provisionado na <b>primeira ativação</b> (ver {@link #alternarStatus}).
 */
@Service
public class IncubadoraService {

    private final IncubadorasRepository incubadoras;
    private final ContasRepository contas;
    private final IncubadoraMapper mapper;
    private final TenantSchemaProvisioner provisioner;

    public IncubadoraService(IncubadorasRepository incubadoras,
                             ContasRepository contas,
                             IncubadoraMapper mapper,
                             TenantSchemaProvisioner provisioner) {
        this.incubadoras = incubadoras;
        this.contas = contas;
        this.mapper = mapper;
        this.provisioner = provisioner;
    }

    @Transactional(readOnly = true)
    public List<IncubadoraResumoDTO> listar(String nome, EStatusIncubadora status) {
        String filtroNome = (nome != null && !nome.isBlank()) ? nome : null;
        String filtroStatus = status != null ? status.name() : null;
        return incubadoras.listarResumo(filtroNome, filtroStatus).stream()
                .map(linha -> new IncubadoraResumoDTO(
                        ((Number) linha[0]).longValue(),
                        (String) linha[1],
                        (String) linha[2],
                        (String) linha[3],
                        ((Number) linha[4]).longValue(),
                        EStatusIncubadora.valueOf((String) linha[5])))
                .toList();
    }

    @Transactional(readOnly = true)
    public IncubadoraDTO buscar(Long id) {
        return montarDTO(carregar(id));
    }

    /** {@code id == null} ⇒ nova incubadora (Aguardando ativação); senão, atualiza. */
    @Transactional(rollbackFor = Exception.class)
    public IncubadoraDTO salvar(IncubadoraDTO dto, Long id) {
        IncubadorasModel model = (id == null) ? new IncubadorasModel() : carregar(id);
        mapper.atualizar(dto, model);
        if (id == null) {
            model.setStatus(EStatusIncubadora.AGUARDANDO_ATIVACAO);
            model.setCriadaEm(LocalDateTime.now());
            model.setNomeSchema(gerarNomeSchema(dto.nome()));
        }
        incubadoras.save(model);
        return montarDTO(model);
    }

    /**
     * Ativa (Suspensa/Aguardando → Em operação) ou suspende (Em operação → Suspensa).
     * Na <b>primeira</b> ativação, provisiona o schema do tenant e marca {@code ativadaEm}.
     */
    @Transactional(rollbackFor = Exception.class)
    public IncubadoraDTO alternarStatus(Long id) {
        IncubadorasModel model = carregar(id);
        if (model.getStatus() == EStatusIncubadora.EM_OPERACAO) {
            model.setStatus(EStatusIncubadora.SUSPENSA);
        } else {
            model.setStatus(EStatusIncubadora.EM_OPERACAO);
            if (model.getAtivadaEm() == null) {
                provisioner.criarSchemaECarregarDDL(model.getNomeSchema());
                model.setAtivadaEm(LocalDateTime.now());
            }
        }
        incubadoras.save(model);
        return montarDTO(model);
    }

    @Transactional(readOnly = true)
    public long contarAtivas() {
        return incubadoras.countByStatus(EStatusIncubadora.EM_OPERACAO);
    }

    /**
     * Incubadora do usuário logado ({@code CONTAS.INC_COD} → INCUBADORAS). Área da própria
     * incubadora ("Minha Incubadora"): não é admin, resolve pela conta da sessão.
     */
    @Transactional(readOnly = true)
    public IncubadoraDTO minhaIncubadora(Long ctaCod) {
        ContasModel conta = contas.findById(ctaCod)
                .orElseThrow(() -> new NaoEncontradoException("Conta", ctaCod));
        if (conta.getIncCod() == null) {
            throw new AcessoNegadoException("Conta sem incubadora vinculada.");
        }
        return montarDTO(carregar(conta.getIncCod()));
    }

    /**
     * Edição da própria incubadora (não admin), resolvida pela conta da sessão. Só os campos
     * institucionais editáveis pela incubadora: {@code nivel}, {@code status}, o responsável, o
     * schema e as datas são preservados (não vêm deste formulário).
     */
    @Transactional(rollbackFor = Exception.class)
    public IncubadoraDTO atualizarMinha(Long ctaCod, IncubadoraDTO dto) {
        ContasModel conta = contas.findById(ctaCod)
                .orElseThrow(() -> new NaoEncontradoException("Conta", ctaCod));
        if (conta.getIncCod() == null) {
            throw new AcessoNegadoException("Conta sem incubadora vinculada.");
        }
        IncubadorasModel model = carregar(conta.getIncCod());
        model.setNome(dto.nome());
        model.setCnpj(dto.cnpj());
        model.setMantenedora(dto.mantenedora());
        model.setEmail(dto.email());
        model.setTelefone(dto.telefone());
        model.setCidade(dto.cidade());
        incubadoras.save(model);
        return montarDTO(model);
    }

    private IncubadorasModel carregar(Long id) {
        return incubadoras.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Incubadora", id));
    }

    /** Slug do nome válido como schema Postgres ({@code ^[a-z0-9_]+$}), único e ≤ 63 chars. */
    private String gerarNomeSchema(String nome) {
        String base = Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
        if (base.isBlank()) {
            base = "incubadora";
        }
        if (base.length() > 55) {
            base = base.substring(0, 55);
        }
        String candidato = base;
        int sufixo = 2;
        while (incubadoras.existsByNomeSchema(candidato)) {
            candidato = base + "_" + sufixo++;
        }
        return candidato;
    }

    private IncubadoraDTO montarDTO(IncubadorasModel m) {
        String responsavelNome = m.getRespCtaCod() == null ? null
                : contas.findById(m.getRespCtaCod()).map(ContasModel::getNome).orElse(null);
        return new IncubadoraDTO(
                m.getIncCod(), m.getNome(), m.getCnpj(), m.getMantenedora(),
                m.getRespCtaCod(), responsavelNome, m.getEmail(),
                m.getTelefone(), m.getCidade(), m.getNivel(), m.getStatus(), m.getNomeSchema(),
                m.getCriadaEm(), m.getAtivadaEm());
    }
}
