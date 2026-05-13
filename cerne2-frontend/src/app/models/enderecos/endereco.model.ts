export interface EnderecoRequest {
  cidade: string;
  rua: string;
  bairro: string;
  numero: string;
  complemento: string;
  estado: string;
  uf: string;
}

export interface EnderecoResponse extends EnderecoRequest {
  endCod: number;
}
