package com.github.davidpotentini.cerne2.service.metricas;

import com.github.davidpotentini.cerne2.dto.metricas.IndicadoresMetricasDTO;
import com.github.davidpotentini.cerne2.dto.metricas.MetricasDTO;
import com.github.davidpotentini.cerne2.dto.metricas.QuantidadeMensalMetricasDTO;
import com.github.davidpotentini.cerne2.mapper.metricas.IndicadoresMetricasMapper;
import com.github.davidpotentini.cerne2.mapper.metricas.MetricasMapper;
import com.github.davidpotentini.cerne2.mapper.metricas.QuantidadeMensalMetricasMapper;
import com.github.davidpotentini.cerne2.models.metricas.IndicadoresMetricasModel;
import com.github.davidpotentini.cerne2.models.metricas.MetricasModel;
import com.github.davidpotentini.cerne2.repository.metricas.IndicadoresMetricasRepository;
import com.github.davidpotentini.cerne2.repository.metricas.MetricasRepository;
import com.github.davidpotentini.cerne2.repository.metricas.QuantidadeMensalMetricasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MetricasServices {

    private final MetricasRepository metricasRepository;
    private final IndicadoresMetricasRepository indicadoresMetricasRepository;
    private final QuantidadeMensalMetricasRepository quantidadeMensalMetricasRepository;
    private final MetricasMapper metricasMapper;
    private final IndicadoresMetricasMapper indicadoresMetricasMapper;
    private final QuantidadeMensalMetricasMapper quantidadeMensalMetricasMapper;

    public MetricasServices(MetricasRepository metricasRepository,
                            IndicadoresMetricasRepository indicadoresMetricasRepository,
                            QuantidadeMensalMetricasRepository quantidadeMensalMetricasRepository,
                            MetricasMapper metricasMapper,
                            IndicadoresMetricasMapper indicadoresMetricasMapper,
                            QuantidadeMensalMetricasMapper quantidadeMensalMetricasMapper) {
        this.metricasRepository = metricasRepository;
        this.indicadoresMetricasRepository = indicadoresMetricasRepository;
        this.quantidadeMensalMetricasRepository = quantidadeMensalMetricasRepository;
        this.metricasMapper = metricasMapper;
        this.indicadoresMetricasMapper = indicadoresMetricasMapper;
        this.quantidadeMensalMetricasMapper = quantidadeMensalMetricasMapper;
    }

    public MetricasDTO findById(Long metCod){
        return metricasMapper.toDTO(metricasRepository.findById(metCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public List<MetricasDTO> findAll(){
        return metricasMapper.toDTOList(metricasRepository.findAll());
    }

    @Transactional(rollbackFor = Exception.class)
    public MetricasDTO save(MetricasDTO metricasDTO) {
        MetricasModel metrica = metricasRepository.save(metricasMapper.toModel(metricasDTO));

        for (IndicadoresMetricasDTO indicadorDTO : metricasDTO.indicadoresMetricasDTOList()) {
            IndicadoresMetricasModel indicador = indicadoresMetricasRepository.save(
                    indicadoresMetricasMapper.toModel(indicadorDTO, metrica));

            for (QuantidadeMensalMetricasDTO quantidadeDTO : indicadorDTO.quantidadeMensalMetricasDTOList()) {
                quantidadeMensalMetricasRepository.save(
                        quantidadeMensalMetricasMapper.toModel(quantidadeDTO, indicador));
            }
        }

        return metricasMapper.toDTO(metricasRepository.findById(metrica.getMetCod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long metCod) {
        MetricasModel metrica = metricasRepository.findById(metCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        for (IndicadoresMetricasModel indicador : metrica.getIndicadoresMetricasModelList()) {
            quantidadeMensalMetricasRepository.deleteByIndicadoresMetricasModel_IndCod(indicador.getIndCod());
        }

        indicadoresMetricasRepository.deleteAll(metrica.getIndicadoresMetricasModelList());
        metricasRepository.delete(metrica);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteIndicador(Long indCod) {
        IndicadoresMetricasModel indicador = indicadoresMetricasRepository.findById(indCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        quantidadeMensalMetricasRepository.deleteByIndicadoresMetricasModel_IndCod(indicador.getIndCod());
        indicadoresMetricasRepository.delete(indicador);
    }
}
