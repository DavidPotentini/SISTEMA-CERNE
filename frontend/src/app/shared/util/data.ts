const cacheData = new Map<string, Date>();

export function isoParaData(iso: string | null): Date | null {
  if (!iso) return null;
  let d = cacheData.get(iso);
  if (!d) {
    d = new Date(`${iso}T00:00:00`);
    cacheData.set(iso, d);
  }
  return d;
}

export function dataParaIso(d: Date | null): string | null {
  if (!d) return null;
  const p = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`;
}
