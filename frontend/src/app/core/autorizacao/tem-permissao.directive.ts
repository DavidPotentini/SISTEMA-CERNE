import { Directive, TemplateRef, ViewContainerRef, effect, inject, input } from '@angular/core';
import { PermissaoService } from './permissao.service';
import { ENivel, ERecurso } from '../../enums/autorizacao';

/**
 * Diretiva estrutural que renderiza o conteúdo só quando o papel logado tem o nível
 * exigido no recurso. Substitui o esconder/mostrar por papel fixo do projeto antigo.
 *
 * Uso:
 * ```html
 * <button *temPermissao="[ERecurso.Planejamento, ENivel.Edicao]">Novo</button>
 * ```
 * O nível é opcional (default LEITURA).
 */
@Directive({ selector: '[temPermissao]' })
export class TemPermissaoDirective {
  private readonly tpl = inject(TemplateRef<unknown>);
  private readonly vc = inject(ViewContainerRef);
  private readonly perm = inject(PermissaoService);

  readonly temPermissao = input.required<[ERecurso, ENivel?]>();

  constructor() {
    effect(() => {
      const [recurso, nivel] = this.temPermissao();
      this.vc.clear();
      if (this.perm.permite(recurso, nivel ?? ENivel.Leitura)) {
        this.vc.createEmbeddedView(this.tpl);
      }
    });
  }
}
