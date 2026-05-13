import { ObjetivoResponse } from '../planejamento/objetivo.model';

export interface QuantidadeMensalMetricaDTO {
  mesCod: number;
  quantidade: number;
}

export interface IndicadorMetricaDTO {
  indCod: number | null;
  descricao: string;
  meta: number;
  metCod: number | null;
  objetivosDTOResponse: ObjetivoResponse;
  quantidadeMensalMetricasDTOList: QuantidadeMensalMetricaDTO[];
}

export interface MetricaDTO {
  metCod: number | null;
  descricao: string;
  indicadoresMetricasDTOList: IndicadorMetricaDTO[];
}
