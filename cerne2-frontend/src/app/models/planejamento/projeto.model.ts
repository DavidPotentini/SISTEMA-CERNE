export interface ProjetoResponse {
  prjCod:      number;
  nome:        string;
  dataInicio:  string;
  dataTermino: string;
  pesCod:      number;
}

export interface ProjetoRequest {
  prjCod?:     number;
  nome:        string;
  dataInicio:  string;
  dataTermino: string;
}