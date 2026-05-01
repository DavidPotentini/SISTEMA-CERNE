

export interface ServicoValorAgregadoResponse {
    servCod:             number;
    servTitulo:          string;
    servDesc:            string;
    servCusto:           number;
    servCondContratacao: string;
    servAnexos:          string;
}

export interface ServicoValorAgregadoRequest {
    servTitulo:          string;
    servDesc:            string;
    servCusto:           number;
    servCondContratacao: string;
    servAnexos:          string;
}