package com.github.davidpotentini.cerne2.service.metricas;

import com.github.davidpotentini.cerne2.dto.metricas.IndicadoresMetricasDTO;
import com.github.davidpotentini.cerne2.dto.metricas.MetricasDTO;
import com.github.davidpotentini.cerne2.dto.metricas.QuantidadeMensalMetricasDTO;
import com.github.davidpotentini.cerne2.dto.planejamentoestrategico.response.ObjetivosDTOResponse;
import com.github.davidpotentini.cerne2.models.metricas.IndicadoresMetricasModel;
import com.github.davidpotentini.cerne2.models.metricas.MetricasModel;
import com.github.davidpotentini.cerne2.models.metricas.QuantidadeMensalMetricasModel;
import com.github.davidpotentini.cerne2.models.metricas.ids.QuantidadeMensalMetricasId;
import com.github.davidpotentini.cerne2.repository.metricas.IndicadoresMetricasRepository;
import com.github.davidpotentini.cerne2.repository.metricas.MetricasRepository;
import com.github.davidpotentini.cerne2.repository.metricas.QuantidadeMensalMetricasRepository;
import com.github.davidpotentini.cerne2.repository.planejamentoestrategico.ObjetivosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MetricasServices {

    private final MetricasRepository metricasRepository;
    private final IndicadoresMetricasRepository indicadoresMetricasRepository;
    private final QuantidadeMensalMetricasRepository quantidadeMensalMetricasRepository;
    private final ObjetivosRepository objetivosRepository;

    public MetricasServices(MetricasRepository metricasRepository,
                            IndicadoresMetricasRepository indicadoresMetricasRepository,
                            QuantidadeMensalMetricasRepository quantidadeMensalMetricasRepository,
                            ObjetivosRepository objetivosRepository) {
        this.metricasRepository = metricasRepository;
        this.indicadoresMetricasRepository = indicadoresMetricasRepository;
        this.quantidadeMensalMetricasRepository = quantidadeMensalMetricasRepository;
        this.objetivosRepository = objetivosRepository;
    }

    public MetricasDTO findById(Long metCod){
        return mapToMetricasDTO(metricasRepository.findById(metCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }


    public List<MetricasDTO> findAll(){
        List<MetricasModel> metricasModelList = metricasRepository.findAll();

        List<MetricasDTO> metricasDTOList = new ArrayList<>();

        for (MetricasModel m: metricasModelList){
            metricasDTOList.add(mapToMetricasDTO(m));
        }

        return metricasDTOList;
    }

    @Transactional(rollbackFor = Exception.class)
    public MetricasDTO save(MetricasDTO metricasDTO) {
        MetricasModel metrica = metricasRepository.save(mapToMetricasModel(metricasDTO));

        for (IndicadoresMetricasDTO indicadorDTO : metricasDTO.indicadoresMetricasDTOList()) {
            IndicadoresMetricasModel indicador = indicadoresMetricasRepository.save(
                    mapToIndicadoresMetricasModel(indicadorDTO, metrica));

            for (QuantidadeMensalMetricasDTO quantidadeDTO : indicadorDTO.quantidadeMensalMetricasDTOList()) {
                quantidadeMensalMetricasRepository.save(
                        mapToQuantidadesMensalMetricasModel(quantidadeDTO, indicador));
            }
        }

        return mapToMetricasDTO(metricasRepository.findById(metrica.getMetCod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long metCod) {
        MetricasModel metrica = metricasRepository.findById(metCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        for (IndicadoresMetricasModel indicador : metrica.getIndicadoresMetricasModelList()) {
            quantidadeMensalMetricasRepository.deleteAllByIndCod(indicador.getIndCod());
        }

        indicadoresMetricasRepository.deleteAll(metrica.getIndicadoresMetricasModelList());
        metricasRepository.delete(metrica);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteIndicador(Long indCod) {
        IndicadoresMetricasModel indicador = indicadoresMetricasRepository.findById(indCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        quantidadeMensalMetricasRepository.deleteAllByIndCod(indicador.getIndCod());
        indicadoresMetricasRepository.delete(indicador);
    }

    private MetricasModel mapToMetricasModel(MetricasDTO metricasDTO) {
        MetricasModel metricasModel = new MetricasModel();

        metricasModel.setMetCod(metricasDTO.metCod());
        metricasModel.setDescricao(metricasDTO.descricao());

        return metricasModel;
    }

    private MetricasDTO mapToMetricasDTO(MetricasModel metricasModel) {
        List<IndicadoresMetricasDTO> indicadoresMetricasDTOList = new ArrayList<>();

        if (metricasModel.getIndicadoresMetricasModelList() != null) {
            for (IndicadoresMetricasModel indicador : metricasModel.getIndicadoresMetricasModelList()) {
                indicadoresMetricasDTOList.add(mapToIndicadoresMetricasDTO(indicador));
            }
        }

        return new MetricasDTO(metricasModel.getMetCod(),
                metricasModel.getDescricao(),
                indicadoresMetricasDTOList);
    }

    private IndicadoresMetricasModel mapToIndicadoresMetricasModel(IndicadoresMetricasDTO indicadoresMetricasDTO,
                                                                    MetricasModel metricasModel) {
        IndicadoresMetricasModel indicadoresMetricasModel = new IndicadoresMetricasModel();

        indicadoresMetricasModel.setIndCod(indicadoresMetricasDTO.indCod());
        indicadoresMetricasModel.setDescricao(indicadoresMetricasDTO.descricao());
        indicadoresMetricasModel.setMeta(indicadoresMetricasDTO.meta());
        indicadoresMetricasModel.setMetricasModel(metricasModel);

        indicadoresMetricasModel.setObjetivosModel(objetivosRepository
                .findById(indicadoresMetricasDTO.objetivosDTOResponse().objCod())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        return indicadoresMetricasModel;
    }

    private IndicadoresMetricasDTO mapToIndicadoresMetricasDTO(IndicadoresMetricasModel indicadoresMetricasModel) {
        List<QuantidadeMensalMetricasDTO> quantidadeMensalMetricasDTOList = new ArrayList<>();

        if (indicadoresMetricasModel.getQuantidadeMensalMetricasModelList() != null) {
            for (QuantidadeMensalMetricasModel q : indicadoresMetricasModel.getQuantidadeMensalMetricasModelList()) {
                quantidadeMensalMetricasDTOList.add(new QuantidadeMensalMetricasDTO(
                        q.getQuantidadeMensalMetricasId().getMesCod(),
                        q.getQuantidade()));
            }
        }

        return new IndicadoresMetricasDTO(indicadoresMetricasModel.getIndCod(),
                indicadoresMetricasModel.getDescricao(),
                indicadoresMetricasModel.getMeta(),
                indicadoresMetricasModel.getMetricasModel() != null
                        ? indicadoresMetricasModel.getMetricasModel().getMetCod()
                        : null,
                new ObjetivosDTOResponse(indicadoresMetricasModel.getObjetivosModel().getObjCod(),
                        indicadoresMetricasModel.getObjetivosModel().getNome(),
                        indicadoresMetricasModel.getObjetivosModel().getDataInicio(),
                        indicadoresMetricasModel.getObjetivosModel().getDataTermino(),
                        indicadoresMetricasModel.getObjetivosModel().getProjetosModel().getPrjCod()),
                quantidadeMensalMetricasDTOList);
    }

    private QuantidadeMensalMetricasModel mapToQuantidadesMensalMetricasModel(QuantidadeMensalMetricasDTO quantidadeMensalMetricasDTO,
                                                                               IndicadoresMetricasModel indicadoresMetricasModel) {
        QuantidadeMensalMetricasModel quantidadeMensalMetricasModel = new QuantidadeMensalMetricasModel();

        quantidadeMensalMetricasModel.setIndicadoresMetricasModel(indicadoresMetricasModel);
        quantidadeMensalMetricasModel.setQuantidade(quantidadeMensalMetricasDTO.quantidade());

        QuantidadeMensalMetricasId quantidadeMensalMetricasId = new QuantidadeMensalMetricasId();
        quantidadeMensalMetricasId.setIndCod(indicadoresMetricasModel.getIndCod());
        quantidadeMensalMetricasId.setMesCod(quantidadeMensalMetricasDTO.mesCod());

        quantidadeMensalMetricasModel.setQuantidadeMensalMetricasId(quantidadeMensalMetricasId);

        return quantidadeMensalMetricasModel;
    }
}
