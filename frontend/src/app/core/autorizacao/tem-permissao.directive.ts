import { Directive, TemplateRef, ViewContainerRef, effect, inject, input } from '@angular/core';
import { PermissaoService } from './permissao.service';
import { ENivel, ERecurso } from '../../enums/autorizacao';

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
