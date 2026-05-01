export interface ObjetivoResponse {
  objCod:      number;
  nome:        string;
  dataInicio:  string;
  dataTermino: string;
  prjCod:      number;
}

export interface ObjetivoRequest {
  nome:        string;
  dataInicio:  string;
  dataTermino: string;
}