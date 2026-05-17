package com.github.davidpotentini.cerne2.mapper;

import com.github.davidpotentini.cerne2.models.canvas.AmbienteCanvasModel;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.ObjetivosModel;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.PlanejamentoEstrategicoModel;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.ProjetosModel;
import com.github.davidpotentini.cerne2.models.planejamentoestrategico.TarefasModel;
import com.github.davidpotentini.cerne2.models.validacaohipotese.QuadroValidacaoHipoteseModel;
import com.github.davidpotentini.cerne2.repository.canvas.AmbienteCanvasRepository;
import com.github.davidpotentini.cerne2.repository.informacoesgeraisincubadas.IncubadasRepository;
import com.github.davidpotentini.cerne2.repository.pessoas.PessoasRepository;
import com.github.davidpotentini.cerne2.repository.planejamentoestrategico.ObjetivosRepository;
import com.github.davidpotentini.cerne2.repository.planejamentoestrategico.PlanejamentoEstrategicoRepository;
import com.github.davidpotentini.cerne2.repository.planejamentoestrategico.ProjetosRepository;
import com.github.davidpotentini.cerne2.repository.planejamentoestrategico.TarefasRepository;
import com.github.davidpotentini.cerne2.repository.validacaohipotese.QuadroValidacaoHipoteseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityReferenceResolver {
    private final IncubadasRepository incubadasRepository;
    private final AmbienteCanvasRepository ambienteCanvasRepository;
    private final QuadroValidacaoHipoteseRepository quadroValidacaoHipoteseRepository;
    private final PlanejamentoEstrategicoRepository planejamentoEstrategicoRepository;
    private final ProjetosRepository projetosRepository;
    private final ObjetivosRepository objetivosRepository;
    private final TarefasRepository tarefasRepository;
    private final PessoasRepository pessoasRepository;

    public IncubadasModel toIncubadasModel(Long incCod){
        return incCod == null? null: incubadasRepository.getReferenceById(incCod);
    }

    public AmbienteCanvasModel toAmbienteCanvasModel(Long ambcCod){
        return ambcCod == null? null: ambienteCanvasRepository.getReferenceById(ambcCod);
    }

    public QuadroValidacaoHipoteseModel toQuadroValidacaoHipotese(Long qvhCod){
        return qvhCod == null? null: quadroValidacaoHipoteseRepository.getReferenceById(qvhCod);
    }

    public PlanejamentoEstrategicoModel toPlanejamentoEstrategicoModel(Long pesCod){
        return pesCod == null? null: planejamentoEstrategicoRepository.getReferenceById(pesCod);
    }

    public ProjetosModel toProjetosModel(Long prjCod){
        return prjCod == null? null: projetosRepository.getReferenceById(prjCod);
    }

    public ObjetivosModel toObjetivosModel(Long objCod){
        return objCod == null? null: objetivosRepository.getReferenceById(objCod);
    }

    public TarefasModel toTarefasModel(Long trfCod){
        return trfCod == null? null: tarefasRepository.getReferenceById(trfCod);
    }

    public PessoasModel toPessoasModel(Long pessoaCod){
        return pessoaCod == null? null: pessoasRepository.getReferenceById(pessoaCod);
    }
}
