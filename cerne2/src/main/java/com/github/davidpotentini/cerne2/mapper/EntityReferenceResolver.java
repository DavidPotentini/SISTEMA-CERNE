package com.github.davidpotentini.cerne2.mapper;

import com.github.davidpotentini.cerne2.models.canvas.AmbienteCanvasModel;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import com.github.davidpotentini.cerne2.models.validacaohipotese.QuadroValidacaoHipoteseModel;
import com.github.davidpotentini.cerne2.repository.canvas.AmbienteCanvasRepository;
import com.github.davidpotentini.cerne2.repository.informacoesgeraisincubadas.IncubadasRepository;
import com.github.davidpotentini.cerne2.repository.validacaohipotese.QuadroValidacaoHipoteseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityReferenceResolver {
    private final IncubadasRepository incubadasRepository;
    private final AmbienteCanvasRepository ambienteCanvasRepository;
    private final QuadroValidacaoHipoteseRepository quadroValidacaoHipoteseRepository;

    public IncubadasModel toIncubadasModel(Long incCod){
        return incCod == null? null: incubadasRepository.getReferenceById(incCod);
    }

    public AmbienteCanvasModel toAmbienteCanvasModel(Long ambcCod){
        return ambcCod == null? null: ambienteCanvasRepository.getReferenceById(ambcCod);
    }

    public QuadroValidacaoHipoteseModel toQuadroValidacaoHipotese(Long qvhCod){
        return qvhCod == null? null: quadroValidacaoHipoteseRepository.getReferenceById(qvhCod);
    }
}
