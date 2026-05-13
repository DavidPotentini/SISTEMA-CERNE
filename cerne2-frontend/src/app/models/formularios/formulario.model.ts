export type TipoCampo = 'texto' | 'numero' | 'data' | 'select' | 'checkbox';

export interface Campo {
  nome: string;
  tipo: TipoCampo;
  obrigatorio: boolean;
  opcoes?: string[];
}

export interface FormularioConfig {
  campos: Campo[];
}

export interface FormularioDTO {
  frmCod: number | null;
  nome: string;
  proposito: string;
  jsonConfig: FormularioConfig;
}
