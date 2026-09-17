package com.github.davidpotentini.service.indicador;

import com.github.davidpotentini.comum.erro.NaoEncontradoException;
import com.github.davidpotentini.comum.erro.RegraNegocioException;
import com.github.davidpotentini.dto.indicador.MetaDTO;
import com.github.davidpotentini.mapper.indicador.MetaMapper;
import com.github.davidpotentini.model.indicador.MetaModel;
import com.github.davidpotentini.repository.indicador.IndicadorRepository;
import com.github.davidpotentini.repository.indicador.MetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MetaService {

    private final MetaRepository metas;
    private final IndicadorRepository indicadores;
    private final MetaMapper mapper;

    public MetaService(MetaRepository metas, IndicadorRepository indicadores, MetaMapper mapper) {
        this.metas = metas;
        this.indicadores = indicadores;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<MetaDTO> listar(Long indCod) {
        exigirIndicador(indCod);
        List<MetaDTO> lista = new ArrayList<>();
        for (MetaModel meta : metas.findByIndCodOrderByDataInicioApuracaoAscMetCodAsc(indCod)) {
            lista.add(mapper.toDTO(meta));
        }
        return lista;
    }

    @Transactional(rollbackFor = Exception.class)
    public MetaDTO criar(Long indCod, MetaDTO dto) {
        exigirIndicador(indCod);
        validarJanela(dto);
        MetaModel meta = mapper.toModel(dto);
        meta.setIndCod(indCod);
        metas.save(meta);
        return mapper.toDTO(meta);
    }

    @Transactional(rollbackFor = Exception.class)
    public MetaDTO editar(Long indCod, Long metCod, MetaDTO dto) {
        validarJanela(dto);
        MetaModel meta = buscarDoIndicador(indCod, metCod);
        mapper.atualizar(dto, meta);
        metas.save(meta);
        return mapper.toDTO(meta);
    }

    @Transactional(rollbackFor = Exception.class)
    public void remover(Long indCod, Long metCod) {
        MetaModel meta = buscarDoIndicador(indCod, metCod);
        metas.delete(meta);
    }


    private void validarJanela(MetaDTO dto) {
        if (dto.dataFimApuracao().isBefore(dto.dataInicioApuracao())) {
            throw new RegraNegocioException("O fim da apuração não pode ser antes do início.");
        }
    }

    private void exigirIndicador(Long indCod) {
        if (!indicadores.existsById(indCod)) {
            throw new NaoEncontradoException("Indicador", indCod);
        }
    }

    private MetaModel buscarDoIndicador(Long indCod, Long metCod) {
        MetaModel meta = metas.findById(metCod)
                .orElseThrow(() -> new NaoEncontradoException("Meta", metCod));
        if (!meta.getIndCod().equals(indCod)) {
            throw new RegraNegocioException("A meta não pertence a este indicador.");
        }
        return meta;
    }
}
