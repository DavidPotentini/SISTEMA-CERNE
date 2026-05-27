package com.github.davidpotentini.cerne2.service.validacaohipotese;

import com.github.davidpotentini.cerne2.dto.validacaohipotese.HipoteseDTO;
import com.github.davidpotentini.cerne2.dto.validacaohipotese.QuadroValidacaoHipoteseDTO;
import com.github.davidpotentini.cerne2.mapper.validacaohipotese.HipoteseMapper;
import com.github.davidpotentini.cerne2.mapper.validacaohipotese.QuadroValidacaoHipoteseMapper;
import com.github.davidpotentini.cerne2.repository.validacaohipotese.HipoteseRepository;
import com.github.davidpotentini.cerne2.repository.validacaohipotese.QuadroValidacaoHipoteseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ValidacaoHipoteseService {

    private final QuadroValidacaoHipoteseRepository quadroValidacaoHipoteseRepository;
    private final HipoteseRepository hipoteseRepository;
    private final QuadroValidacaoHipoteseMapper quadroValidacaoHipoteseMapper;
    private final HipoteseMapper hipoteseMapper;

    public ValidacaoHipoteseService(QuadroValidacaoHipoteseRepository quadroValidacaoHipoteseRepository,
                                    HipoteseRepository hipoteseRepository,
                                    QuadroValidacaoHipoteseMapper quadroValidacaoHipoteseMapper,
                                    HipoteseMapper hipoteseMapper) {
        this.quadroValidacaoHipoteseRepository = quadroValidacaoHipoteseRepository;
        this.hipoteseRepository = hipoteseRepository;
        this.quadroValidacaoHipoteseMapper = quadroValidacaoHipoteseMapper;
        this.hipoteseMapper = hipoteseMapper;
    }

    /*QUADRO VALIDAÇÃO HIPOTESE*/

    public List<QuadroValidacaoHipoteseDTO> findQuadroValidacaoHipoteseByIncCod(Long incCod){
        return quadroValidacaoHipoteseMapper.toDTOList(quadroValidacaoHipoteseRepository.findQuadroValidacaoHipoteseByIncCod(incCod));
    }

    public QuadroValidacaoHipoteseDTO findQuadroValidacaoHipoteeById(Long qvhCod){
        return quadroValidacaoHipoteseMapper.toDTO(quadroValidacaoHipoteseRepository.findById(qvhCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public QuadroValidacaoHipoteseDTO saveQuadroValidacaoHipotese(QuadroValidacaoHipoteseDTO quadroValidacaoHipoteseDTO, Long qvhCod){
        return quadroValidacaoHipoteseMapper.toDTO(quadroValidacaoHipoteseRepository.save(
                quadroValidacaoHipoteseMapper.toModel(quadroValidacaoHipoteseDTO, qvhCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteQuadroValidacaoHipotese(Long qvhCod){
        hipoteseRepository.deleteHipoteseByQvhCod(qvhCod);
        quadroValidacaoHipoteseRepository.deleteById(qvhCod);
    }


    /*HIPOTESE*/

    public List<HipoteseDTO> findHipoteseByQvhCod(Long qvhCod){
        return hipoteseMapper.toDTOList(hipoteseRepository.findHipoteseByQvhCod(qvhCod));
    }

    @Transactional(rollbackFor = Exception.class)
    public HipoteseDTO saveHipotese(HipoteseDTO hipoteseDTO, Long qvhCod, Long hipCod){
        return hipoteseMapper.toDTO(hipoteseRepository.save(hipoteseMapper.toModel(hipoteseDTO, hipCod, qvhCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteHipotese(Long hipCod){
        hipoteseRepository.deleteById(hipCod);
    }
}
