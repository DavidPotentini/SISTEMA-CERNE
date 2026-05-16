export interface CampoCanvas {
  texto: string;
}

export interface AmbienteCanvasDTO {
  ambcCod: number | null;
  descricao: string;
  incCod: number;
}

export interface BusinessModelCanvasDTO {
  bmcCod: number | null;
  ambcCod: number | null;
  parceirosChave: CampoCanvas;
  atividadesChave: CampoCanvas;
  propostasValor: CampoCanvas;
  relacionamentoCliente: CampoCanvas;
  segmentoCliente: CampoCanvas;
  canais: CampoCanvas;
  recursosChave: CampoCanvas;
  estruturaCustos: CampoCanvas;
  fontesReceitas: CampoCanvas;
}

export interface LeanCanvasDTO {
  leanCod: number | null;
  ambcCod: number | null;
  problema: CampoCanvas;
  solucao: CampoCanvas;
  metrica: CampoCanvas;
  propostaValor: CampoCanvas;
  competenciaEssencial: CampoCanvas;
  canais: CampoCanvas;
  segmentoCliente: CampoCanvas;
  estruturaCustos: CampoCanvas;
  modeloReceita: CampoCanvas;
}

export interface ValuePropositionCanvasDTO {
  vpcCod: number | null;
  ambcCod: number | null;
  criadoresGanho: CampoCanvas;
  produtosServicos: CampoCanvas;
  alivioDores: CampoCanvas;
  ganhos: CampoCanvas;
  dores: CampoCanvas;
  tarefasClientes: CampoCanvas;
}

export interface CustomerPersonasCanvasDTO {
  ambcCod: number | null;
  personaCod: number | null;
  atributos: {
    nome: string;
    descricao: string;
    itens: { rotulo: string; valor: string }[];
  };
}

export interface ChannelImplementationCanvasDTO {
  ambcCod: number | null;
  segCod: number | null;
  nomeSegmento: string;
  atividadeConhecimento: CampoCanvas;
  atividadeAvaliacao: CampoCanvas;
  atividadeCompra: CampoCanvas;
  atividadeEntrega: CampoCanvas;
  atividadePosVenda: CampoCanvas;
  recursosConhecimento: CampoCanvas;
  recursosAvaliacao: CampoCanvas;
  recursosCompra: CampoCanvas;
  recursosEntrega: CampoCanvas;
  recursosPosVenda: CampoCanvas;
  parceirosConhecimento: CampoCanvas;
  parceirosAvaliacao: CampoCanvas;
  parceirosCompra: CampoCanvas;
  parceirosEntrega: CampoCanvas;
  parceirosPosVenda: CampoCanvas;
}

export function campoVazio(): CampoCanvas {
  return { texto: '' };
}

export function bmcVazio(ambcCod: number | null = null): BusinessModelCanvasDTO {
  return {
    bmcCod: null,
    ambcCod,
    parceirosChave: campoVazio(),
    atividadesChave: campoVazio(),
    propostasValor: campoVazio(),
    relacionamentoCliente: campoVazio(),
    segmentoCliente: campoVazio(),
    canais: campoVazio(),
    recursosChave: campoVazio(),
    estruturaCustos: campoVazio(),
    fontesReceitas: campoVazio(),
  };
}

export function leanVazio(ambcCod: number | null = null): LeanCanvasDTO {
  return {
    leanCod: null,
    ambcCod,
    problema: campoVazio(),
    solucao: campoVazio(),
    metrica: campoVazio(),
    propostaValor: campoVazio(),
    competenciaEssencial: campoVazio(),
    canais: campoVazio(),
    segmentoCliente: campoVazio(),
    estruturaCustos: campoVazio(),
    modeloReceita: campoVazio(),
  };
}

export function vpcVazio(ambcCod: number | null = null): ValuePropositionCanvasDTO {
  return {
    vpcCod: null,
    ambcCod,
    criadoresGanho: campoVazio(),
    produtosServicos: campoVazio(),
    alivioDores: campoVazio(),
    ganhos: campoVazio(),
    dores: campoVazio(),
    tarefasClientes: campoVazio(),
  };
}

export function personaVazia(ambcCod: number | null = null): CustomerPersonasCanvasDTO {
  return {
    ambcCod,
    personaCod: null,
    atributos: { nome: '', descricao: '', itens: [] },
  };
}

export function channelImplVazio(ambcCod: number | null = null): ChannelImplementationCanvasDTO {
  return {
    ambcCod,
    segCod: null,
    nomeSegmento: '',
    atividadeConhecimento: campoVazio(),
    atividadeAvaliacao: campoVazio(),
    atividadeCompra: campoVazio(),
    atividadeEntrega: campoVazio(),
    atividadePosVenda: campoVazio(),
    recursosConhecimento: campoVazio(),
    recursosAvaliacao: campoVazio(),
    recursosCompra: campoVazio(),
    recursosEntrega: campoVazio(),
    recursosPosVenda: campoVazio(),
    parceirosConhecimento: campoVazio(),
    parceirosAvaliacao: campoVazio(),
    parceirosCompra: campoVazio(),
    parceirosEntrega: campoVazio(),
    parceirosPosVenda: campoVazio(),
  };
}
