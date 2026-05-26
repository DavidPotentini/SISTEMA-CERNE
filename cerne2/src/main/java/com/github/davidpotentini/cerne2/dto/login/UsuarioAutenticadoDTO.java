package com.github.davidpotentini.cerne2.dto.login;


public record UsuarioAutenticadoDTO(Long usnCod,
                                    Long pessoaCod,
                                    String email,
                                    Long tntCod,
                                    String nomeSchema,
                                    String role,
                                    Long incCod) {
}
