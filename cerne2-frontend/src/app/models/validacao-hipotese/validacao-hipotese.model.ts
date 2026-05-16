import { EBlocoLeanCanvas } from '../../enums/bloco-lean-canvas.enum';
import {
  EDecisaoHipotese,
  EResultadoHipotese,
  EStatusHipotese,
} from '../../enums/hipotese.enum';

export interface HipoteseDTO {
  hipCod: number | null;
  tituloHipotese: string;
  vldBlocoLeanCanvas: EBlocoLeanCanvas | null;
  hipotese: string;
  experimento: string;
  metrica: string;
  vldStatusHipotese: EStatusHipotese | null;
  vldResultadoHipotese: EResultadoHipotese | null;
  resultadoDetalhamento: string;
  vldDecisaoHipotese: EDecisaoHipotese | null;
  decisaoDetalhamento: string;
  qvhCod: string | null;
}

export interface QuadroValidacaoHipoteseDTO {
  qvhCod: number | null;
  tituloQuadro: string;
  incCod: number;
  hipoteseDTOList: HipoteseDTO[];
}

export function hipoteseVazia(qvhCod: number | null = null): HipoteseDTO {
  return {
    hipCod: null,
    tituloHipotese: '',
    vldBlocoLeanCanvas: null,
    hipotese: '',
    experimento: '',
    metrica: '',
    vldStatusHipotese: EStatusHipotese.EM_DESENVOLVIMENTO,
    vldResultadoHipotese: null,
    resultadoDetalhamento: '',
    vldDecisaoHipotese: null,
    decisaoDetalhamento: '',
    qvhCod: qvhCod == null ? null : String(qvhCod),
  };
}

export function quadroVazio(incCod: number): QuadroValidacaoHipoteseDTO {
  return {
    qvhCod: null,
    tituloQuadro: '',
    incCod,
    hipoteseDTOList: [],
  };
}
