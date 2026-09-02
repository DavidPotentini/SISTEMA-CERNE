import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { IncubadoraService } from '../../core/services/incubadora/incubadora.service';
import {
  EStatusIncubadora,
  IncubadoraResumo,
  STATUS_LABEL,
} from '../../models/incubadora/incubadora.model';
import { reterRecurso } from '../../shared/util/reter-recurso';

/**
 * Painel da plataforma: cards com as contagens por status e a listagem das incubadoras
 * (mesma da tela "Incubadoras", sem ações). As contagens saem do próprio array — a
 * listagem já traz todas as incubadoras com o status.
 */
@Component({
  selector: 'app-painel',
  imports: [RouterLink, MatButtonModule, MatCardModule, MatTableModule],
  templateUrl: './painel.component.html',
  styleUrl: './painel.component.css',
})
export class PainelComponent {
  private readonly service = inject(IncubadoraService);

  readonly colunas = ['nome', 'mantenedora', 'responsavel', 'usuarios', 'status'];

  /** Refaz a busca sempre que houver mutação em incubadoras (ativar/suspender/salvar). */
  readonly dados = reterRecurso(rxResource({
    params: () => ({ versao: this.service.versao() }),
    stream: () => this.service.listar(),
  }));

  private lista(): IncubadoraResumo[] {
    return this.dados.value() ?? [];
  }

  total(): number {
    return this.lista().length;
  }

  ativas(): number {
    return this.contar('EM_OPERACAO');
  }

  aguardando(): number {
    return this.contar('AGUARDANDO_ATIVACAO');
  }

  suspensas(): number {
    return this.contar('SUSPENSA');
  }

  private contar(status: EStatusIncubadora): number {
    return this.lista().filter(i => i.status === status).length;
  }

  label(s: EStatusIncubadora): string {
    return STATUS_LABEL[s];
  }
}
