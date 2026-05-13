package com.github.davidpotentini.cerne2.service.pessoas;

import com.github.davidpotentini.cerne2.dto.pessoas.request.PessoasDTORequest;
import com.github.davidpotentini.cerne2.dto.pessoas.response.PessoasDTOResponse;
import com.github.davidpotentini.cerne2.enums.ETipoEmpreendimento;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
import com.github.davidpotentini.cerne2.repository.informacoesgeraisincubadas.IncubadasRepository;
import com.github.davidpotentini.cerne2.repository.pessoas.PessoasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PessoasService {

    private final PessoasRepository pessoasRepository;
    private final IncubadasRepository incubadasRepository;

    public PessoasService(PessoasRepository pessoasRepository, IncubadasRepository incubadasRepository) {
        this.pessoasRepository = pessoasRepository;
        this.incubadasRepository = incubadasRepository;
    }

    public List<PessoasDTOResponse> findByIncubadora(){
        List<PessoasModel> pessoasModelList = pessoasRepository.findByTipoEmpreendimento(ETipoEmpreendimento.INCUBADORA)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<PessoasDTOResponse> pessoasDTOResponseList = new ArrayList<>();

        for (PessoasModel p: pessoasModelList){
            pessoasDTOResponseList.add(mapToPessoasDTO(p));
        }

        return pessoasDTOResponseList;
    }

    public List<PessoasDTOResponse> findByIncubada(Long incCod){
        List<PessoasModel> pessoasModelList = pessoasRepository.findByIncubadasModel_IncCod(incCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<PessoasDTOResponse> pessoasDTOResponseList = new ArrayList<>();

        for (PessoasModel p: pessoasModelList){
            pessoasDTOResponseList.add(mapToPessoasDTO(p));
        }

        return pessoasDTOResponseList;
    }

    public PessoasDTOResponse findById(Long pessoaCod){
        return mapToPessoasDTO(pessoasRepository.findById(pessoaCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public PessoasDTOResponse save(PessoasDTORequest pessoasDTORequest, Long pessoaCod){
        return mapToPessoasDTO(pessoasRepository.save(mapToPessoasModel(pessoasDTORequest, pessoaCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long pessoaCod){
        pessoasRepository.deleteById(pessoaCod);
    }

    private PessoasModel mapToPessoasModel(PessoasDTORequest pessoasDTORequest, Long pessoaCod){
        PessoasModel pessoasModel = new PessoasModel();

        pessoasModel.setPessoaCod(pessoaCod);
        pessoasModel.setNome(pessoasDTORequest.nome());
        pessoasModel.setEmail(pessoasDTORequest.email());
        pessoasModel.setCpf(pessoasDTORequest.cpf());
        pessoasModel.setCargo(pessoasDTORequest.cargo());

        if (pessoasDTORequest.incCod() != null) {
            incubadasRepository.findById(pessoasDTORequest.incCod())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }

        pessoasModel.setTipoEmpreendimento(pessoasDTORequest.eTipoEmpreendimento());

        return pessoasModel;
    }

    private PessoasDTOResponse mapToPessoasDTO(PessoasModel pessoasModel){
        IncubadasModel incubadasModel = pessoasModel.getIncubadasModel();
        Long incCod = null;

        if (incubadasModel != null){
            incCod = incubadasModel.getIncCod();
        }

        return new PessoasDTOResponse(
                pessoasModel.getPessoaCod(),
                pessoasModel.getNome(),
                pessoasModel.getEmail(),
                pessoasModel.getCpf(),
                pessoasModel.getCargo(),
                incCod,
                pessoasModel.getTipoEmpreendimento()
        );
    }
}
