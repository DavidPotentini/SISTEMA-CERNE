package com.github.davidpotentini.cerne2.service.informacoesgeraisincubadas;

import com.github.davidpotentini.cerne2.dto.arquivo.ArquivoDTO;
import com.github.davidpotentini.cerne2.dto.informacoesgeraisincubadas.IncubadasDTO;
import com.github.davidpotentini.cerne2.mapper.arquivo.ArquivosMapper;
import com.github.davidpotentini.cerne2.mapper.endereco.EnderecoMapper;
import com.github.davidpotentini.cerne2.mapper.informacoesgeraisincubadas.IncubadasMapper;
import com.github.davidpotentini.cerne2.models.arquivo.ArquivosIncubadasModel;
import com.github.davidpotentini.cerne2.models.arquivo.ArquivosModel;
import com.github.davidpotentini.cerne2.models.arquivo.ids.ArquivosIcubadasId;
import com.github.davidpotentini.cerne2.models.informacoesgeraisincubadas.IncubadasModel;
import com.github.davidpotentini.cerne2.repository.arquivo.ArquivosIncubadasRepository;
import com.github.davidpotentini.cerne2.repository.informacoesgeraisincubadas.IncubadasRepository;
import com.github.davidpotentini.cerne2.repository.pessoas.PessoasRepository;
import com.github.davidpotentini.cerne2.service.arquivos.ArquivoService;
import com.github.davidpotentini.cerne2.service.enderecos.EnderecoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class IncubadasService {

    private final IncubadasRepository incubadasRepository;
    private final PessoasRepository pessoasRepository;
    private final EnderecoService enderecoService;
    private final IncubadasMapper incubadasMapper;
    private final EnderecoMapper enderecoMapper;
    private final ArquivosIncubadasRepository arquivosIncubadasRepository;
    private final ArquivoService arquivoService;
    private final ArquivosMapper arquivosMapper;

    public IncubadasService(IncubadasRepository incubadasRepository,
                            PessoasRepository pessoasRepository,
                            EnderecoService enderecoService,
                            IncubadasMapper incubadasMapper,
                            EnderecoMapper enderecoMapper,
                            ArquivosIncubadasRepository arquivosIncubadasRepository,
                            ArquivoService arquivoService,
                            ArquivosMapper arquivosMapper) {
        this.incubadasRepository = incubadasRepository;
        this.pessoasRepository = pessoasRepository;
        this.enderecoService = enderecoService;
        this.incubadasMapper = incubadasMapper;
        this.enderecoMapper = enderecoMapper;
        this.arquivosIncubadasRepository = arquivosIncubadasRepository;
        this.arquivoService = arquivoService;
        this.arquivosMapper = arquivosMapper;
    }

    public List<IncubadasDTO> findList() {
        return incubadasMapper.toDTOList(incubadasRepository.findAll());
    }

    public IncubadasDTO findById(Long incCod) {
        return incubadasMapper.toDTO(incubadasRepository.findById(incCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    //VERIFICAR SE QUANDO ATUALIZA O ENDEREÇO UM NOVO REGISTRO É CRIADO
    @Transactional(rollbackFor = Exception.class)
    public IncubadasDTO
    save(IncubadasDTO incubadasDTO, Long incCod){
        IncubadasModel incubadasModel = incubadasMapper.toModel(incubadasDTO, incCod);

        // Em atualizações reaproveita o endereço já vinculado à incubada.
        Long endCod = incCod == null
                ? null
                : incubadasRepository.findById(incCod)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                    .getEnderecosModel().getEndCod();

        incubadasModel.setEnderecosModel(enderecoMapper.toModel(incubadasDTO.enderecoDTO(), endCod));

        return incubadasMapper.toDTO(incubadasRepository.save(incubadasModel));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long incCod){
        IncubadasModel incubadasModel = incubadasRepository.findById(incCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        enderecoService.delete(incubadasModel.getEnderecosModel().getEndCod());

        pessoasRepository.deleteByIncubadasModel_IncCod(incCod);

        incubadasRepository.delete(incubadasModel);
    }

    public List<ArquivoDTO> findArquivosIncubadas(Long incCod){
        List<ArquivosIncubadasModel> arquivosIncubadasModelList = arquivosIncubadasRepository.findByIncubadasModel_IncCod(incCod);

        List<ArquivosModel> arquivosModelList = new ArrayList<>();

        for (ArquivosIncubadasModel a: arquivosIncubadasModelList){
            arquivosModelList.add(arquivoService.findById(a.getArquivosModel().getArqCod()));
        }

        return arquivosMapper.toDTOList(arquivosModelList);
    }

    @Transactional(rollbackFor = Exception.class)
    public ArquivoDTO uploadArquivosIncubadas(MultipartFile multipartFile, Long incCod){
        ArquivoDTO arquivoDTO = arquivoService.upload(multipartFile);

        ArquivosModel arquivosModel = arquivoService.findById(arquivoDTO.arqCod());
        IncubadasModel incubadasModel = incubadasRepository.findById(incCod)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ArquivosIncubadasModel arquivosIncubadasModel = new ArquivosIncubadasModel();
        arquivosIncubadasModel.setArquivosIcubadasId(new ArquivosIcubadasId());
        arquivosIncubadasModel.setIncubadasModel(incubadasModel);
        arquivosIncubadasModel.setArquivosModel(arquivosModel);

        arquivosIncubadasRepository.save(arquivosIncubadasModel);

        return arquivoDTO;
    }

    /*private List<ArquivosIncubadasModel> returnArquivosIncubadasList(Long incCod){
        return arquivosIncubadasRepository.findByIncubadasModel_IncCod(incCod);
    }*/
}
