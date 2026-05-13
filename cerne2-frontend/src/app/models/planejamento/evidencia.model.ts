export interface EvidenciaResponse {
  evdCod:         number;
  descricao:      string;
  caminhoArquivo: string;
  trfCod:         number;
}

export interface EvidenciaRequest {
  descricao:      string;
  caminhoArquivo: string;
  trfCod:         number;
}