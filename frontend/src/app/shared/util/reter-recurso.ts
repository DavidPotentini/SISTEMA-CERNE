import { Signal, linkedSignal } from '@angular/core';

/** Subconjunto da interface do rxResource que consumimos. */
interface Recurso<T> {
  value: Signal<T | undefined>;
  hasValue(): boolean;
  error(): unknown;
  isLoading(): boolean;
  reload(): boolean;
}

/**
 * Embrulha um rxResource preservando o último valor durante as recargas. Ao bumpar a `versao` numa
 * mutação, o request muda e o resource passa por `loading` com `value()` indefinido — o template
 * cairia no ramo "Carregando", desmontaria o DOM da lista e jogaria a rolagem ao topo. Aqui o `value()`
 * segura o valor anterior até o novo chegar (`@for track` casa em vez de recriar, mantendo a rolagem);
 * quando o dado real chega, ele substitui — o fallback só age enquanto `atual` é indefinido.
 */
export function reterRecurso<T>(recurso: Recurso<T>): Recurso<T> {
  const value = linkedSignal<T | undefined, T | undefined>({
    source: () => (recurso.hasValue() ? recurso.value() : undefined),
    computation: (atual, anterior) => atual ?? anterior?.value,
  });
  return {
    value,
    hasValue: () => value() !== undefined,
    error: () => recurso.error(),
    isLoading: () => recurso.isLoading(),
    reload: () => recurso.reload(),
  };
}
