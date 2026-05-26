package com.github.davidpotentini.cerne2.enums;

/**
 * Papéis de acesso do sistema. O {@link #name()} de cada valor corresponde
 * exatamente ao ROLE_DESCR semeado na tabela ROLES de cada schema de tenant.
 */
public enum ERole {
    ADMIN_INCUBADORA,
    ADMIN_INCUBADA;

    /**
     * Pessoa vinculada a uma incubada (incCod presente) é ADMIN_INCUBADA;
     * qualquer outra é ADMIN_INCUBADORA. O vínculo com a incubada é a fonte de
     * verdade — o campo TIPO_EMPREENDIMENTO descreve a pessoa, não decide papel.
     */
    public static ERole fromIncCod(Long incCod) {
        return incCod != null ? ADMIN_INCUBADA : ADMIN_INCUBADORA;
    }
}
