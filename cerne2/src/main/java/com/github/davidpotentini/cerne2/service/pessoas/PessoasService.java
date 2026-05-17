package com.github.davidpotentini.cerne2.service.pessoas;

import com.github.davidpotentini.cerne2.dto.pessoas.PessoasDTO;
import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;
import com.github.davidpotentini.cerne2.mapper.pessoas.PessoasMapper;
import com.github.davidpotentini.cerne2.repository.pessoas.PessoasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PessoasService {

    private final PessoasRepository pessoasRepository;
    private final PessoasMapper pessoasMapper;

    public PessoasService(PessoasRepository pessoasRepository, PessoasMapper pessoasMapper) {
        this.pessoasRepository = pessoasRepository;
        this.pessoasMapper = pessoasMapper;
    }

    public List<PessoasDTO> findByIncubadora(){
        return pessoasMapper.toDTOList(pessoasRepository.findByTipoEmpreendimento(ETipoEmpreendimento.INCUBADORA)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public List<PessoasDTO> findByIncubada(Long incCod){
        return pessoasMapper.toDTOList(pessoasRepository.findByIncubadasModel_IncCod(incCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public PessoasDTO findById(Long pessoaCod){
        return pessoasMapper.toDTO(pessoasRepository.findById(pessoaCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public PessoasDTO save(PessoasDTO pessoasDTO, Long pessoaCod){
        return pessoasMapper.toDTO(pessoasRepository.save(pessoasMapper.toModel(pessoasDTO, pessoaCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long pessoaCod){
        pessoasRepository.deleteById(pessoaCod);
    }
}
