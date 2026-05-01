export interface PlanejamentoResponse {
  pesCod:      number;
  nome:        string;
  dataInicio:  string;
  dataTermino: string;
}

export interface PlanejamentoRequest {
  nome:        string;
  dataInicio:  string;
  dataTermino: string;
}