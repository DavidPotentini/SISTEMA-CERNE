package com.github.davidpotentini.service.usuarios;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.comum.tenant.TenantContext;
import com.github.davidpotentini.dto.usuarios.PapelResumoDTO;
import com.github.davidpotentini.dto.usuarios.UsuarioConviteDTO;
import com.github.davidpotentini.dto.usuarios.UsuarioEdicaoDTO;
import com.github.davidpotentini.dto.usuarios.UsuarioResumoDTO;
import com.github.davidpotentini.enums.EStatusConta;
import com.github.davidpotentini.model.contas.ContasModel;
import com.github.davidpotentini.model.incubadoras.IncubadorasModel;
import com.github.davidpotentini.repository.contas.ContasRepository;
import com.github.davidpotentini.repository.incubadoras.IncubadorasRepository;
import com.github.davidpotentini.service.multitenancy.AutorizacaoTenantService;
import com.github.davidpotentini.service.multitenancy.AutorizacaoTenantService.PapelPessoa;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * "Usuários da plataforma" do administrador. Opera no schema {@code public} (CONTAS), mas resolve e
 * grava o papel de cada usuário na gaveta do tenant da sua incubadora via {@link AutorizacaoTenantService}.
 */
@Service
public class UsuarioService {

    private final ContasRepository contas;
    private final IncubadorasRepository incubadoras;
    private final AutorizacaoTenantService autorizacao;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(ContasRepository contas,
                          IncubadorasRepository incubadoras,
                          AutorizacaoTenantService autorizacao,
                          PasswordEncoder passwordEncoder) {
        this.contas = contas;
        this.incubadoras = incubadoras;
        this.autorizacao = autorizacao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResumoDTO> listar() {
        // Pré-carrega as incubadoras (nome + schema + ativadaEm) para não fazer N buscas no público.
        Map<Long, IncubadorasModel> porId = new HashMap<>();
        for (IncubadorasModel inc : incubadoras.findAll()) {
            porId.put(inc.getIncCod(), inc);
        }

        List<UsuarioResumoDTO> resumos = new ArrayList<>();
        for (ContasModel conta : contas.findAllByOrderByNomeAsc()) {
            IncubadorasModel inc = conta.getIncCod() == null ? null : porId.get(conta.getIncCod());
            resumos.add(montarResumo(conta, inc));
        }
        return resumos;
    }

    @Transactional(readOnly = true)
    public List<PapelResumoDTO> listarPapeis(Long incCod) {
        IncubadorasModel inc = incubadoras.findById(incCod)
                .orElseThrow(() -> new NaoEncontradoException("Incubadora", incCod));
        if (inc.getAtivadaEm() == null) {
            return List.of();
        }
        return TenantContext.callWithin(inc.getNomeSchema(), autorizacao::listarPapeis);
    }

    @Transactional(rollbackFor = Exception.class)
    public UsuarioResumoDTO convidar(UsuarioConviteDTO dto) {
        if (contas.existsByEmail(dto.email())) {
            throw new RegraNegocioException("E-mail já cadastrado: " + dto.email());
        }
        ContasModel conta = new ContasModel();
        conta.setNome(dto.nome());
        conta.setEmail(dto.email());
        conta.setStatus(EStatusConta.CONVIDADO);
        conta.setAdminPlataforma(false);
        conta.setIncCod(dto.incCod());
        // Senha aleatória inutilizável: o convidado ainda não pode logar (define no aceite).
        conta.setSenha(passwordEncoder.encode(UUID.randomUUID().toString()));
        contas.save(conta);
        vincular(conta.getCtaCod(), dto.incCod(), dto.papCod());
        return montarResumo(conta, buscarIncubadora(conta.getIncCod()));
    }

    @Transactional(rollbackFor = Exception.class)
    public UsuarioResumoDTO editar(Long id, UsuarioEdicaoDTO dto) {
        ContasModel conta = buscarConta(id);
        conta.setNome(dto.nome());
        conta.setIncCod(dto.incCod());
        contas.save(conta);
        vincular(conta.getCtaCod(), dto.incCod(), dto.papCod());
        return montarResumo(conta, buscarIncubadora(conta.getIncCod()));
    }

    @Transactional(rollbackFor = Exception.class)
    public UsuarioResumoDTO alternarStatus(Long id) {
        ContasModel conta = buscarConta(id);

        conta.setStatus(conta.getStatus() == EStatusConta.SUSPENSO
                ? EStatusConta.ATIVO
                : EStatusConta.SUSPENSO);
        contas.save(conta);
        return montarResumo(conta, buscarIncubadora(conta.getIncCod()));
    }

    private void vincular(Long ctaCod, Long incCod, Long papCod) {
        if (incCod == null || papCod == null) {
            return;
        }
        IncubadorasModel inc = incubadoras.findById(incCod)
                .orElseThrow(() -> new NaoEncontradoException("Incubadora", incCod));
        if (inc.getAtivadaEm() == null) {
            throw new RegraNegocioException(
                    "Incubadora ainda não ativada: não há papéis para vincular.");
        }
        TenantContext.runWithin(inc.getNomeSchema(), () -> autorizacao.vincularPapel(ctaCod, papCod));
    }

    private IncubadorasModel buscarIncubadora(Long incCod) {
        if (incCod == null) {
            return null;
        }
        return incubadoras.findById(incCod).orElse(null);
    }

    private ContasModel buscarConta(Long ctaCod) {
        return contas.findById(ctaCod)
                .orElseThrow(() -> new NaoEncontradoException("Usuário", ctaCod));
    }

    private UsuarioResumoDTO montarResumo(ContasModel conta, IncubadorasModel inc) {
        String incubadoraNome = inc == null ? null : inc.getNome();

        Long papCod = null;
        String papel = null;

        if (inc != null && inc.getAtivadaEm() != null) {
            PapelPessoa pp = TenantContext.callWithin(inc.getNomeSchema(),
                    () -> autorizacao.resolverPapelPessoa(conta.getCtaCod()));
            papCod = pp.papCod();
            papel = pp.nome();
        }
        return new UsuarioResumoDTO(conta.getCtaCod(), conta.getNome(), conta.getEmail(),
                conta.getIncCod(), incubadoraNome, papCod, papel, conta.getStatus());
    }
}
