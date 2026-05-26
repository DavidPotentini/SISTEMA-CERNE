package com.github.davidpotentini.cerne2.dto.arquivo;

import java.time.LocalDateTime;

public record ArquivoDTO(Long arqCod,
                         String nomeOriginal,
                         String contentType,
                         Long tamanhoType,
                         LocalDateTime dataUpload) {
}
