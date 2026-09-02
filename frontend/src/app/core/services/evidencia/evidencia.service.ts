import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import {
  AtividadeOpcao,
  EStatusEvidencia,
  Evidencia,
} from '../../../models/evidencia/evidencia.model';

/**
 * Registros de evidência (tenant vem do JWT). Cada evidência é versionada: a listagem traz a versão
 * corrente; ABRIR traz o histórico de versões; registrar cria a versão 1 e corrigir gera a próxima
 * (só quando a corrente está em correção solicitada).
 */
@Injectable({ providedIn: 'root' })
export class EvidenciaService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/incubadora/evidencias`;

  /** Incrementa a cada mutação; a listagem observa para recarregar. */
  readonly versao = signal(0);
  recarregar(): void {
    this.versao.update(v => v + 1);
  }

  /** Versão corrente de cada evidência (listagem, cards e filtro). */
  listar() {
    return this.http.get<Evidencia[]>(this.base);
  }

  /** Atividades do plano vigente (lista plana) para os dropdowns em cascata do cadastro. */
  atividades() {
    return this.http.get<AtividadeOpcao[]>(`${this.base}/atividades`);
  }

  /** Histórico da evidência: todas as versões em ordem, cada uma com seu motivo de correção. */
  historico(evdCod: number) {
    return this.http.get<Evidencia[]>(`${this.base}/${evdCod}`);
  }

  /** Registra uma nova evidência (versão 1). */
  registrar(dto: Partial<Evidencia>) {
    return this.http.post<Evidencia>(this.base, dto);
  }

  /** Corrige a evidência gerando a próxima versão (mesmo id lógico). */
  corrigir(evdCod: number, dto: Partial<Evidencia>) {
    return this.http.post<Evidencia>(`${this.base}/${evdCod}/correcoes`, dto);
  }

  /** Avalia a versão corrente (só se PENDENTE_VALIDACAO): validar ou solicitar correção (motivo obrigatório). */
  avaliar(evdCod: number, status: EStatusEvidencia, motivo: string | null) {
    return this.http.post<Evidencia>(`${this.base}/${evdCod}/avaliacoes`, { status, motivo });
  }
}
