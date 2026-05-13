package com.github.davidpotentini.cerne2.service.enderecos;

import com.github.davidpotentini.cerne2.dto.endereco.request.EnderecoDTORequest;
import com.github.davidpotentini.cerne2.dto.endereco.response.EnderecoDTOResponse;
import com.github.davidpotentini.cerne2.models.enderecos.EnderecosModel;
import com.github.davidpotentini.cerne2.repository.enderecos.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long endCod) {
        enderecoRepository.deleteById(endCod);
    }

    public EnderecosModel mapToEnderecosModel(EnderecoDTORequest enderecoDTORequest, Long endCod){
        EnderecosModel enderecosModel = new EnderecosModel();

        enderecosModel.setEndCod(endCod);
        enderecosModel.setCidade(enderecoDTORequest.cidade());
        enderecosModel.setRua(enderecoDTORequest.rua());
        enderecosModel.setBairro(enderecoDTORequest.bairro());
        enderecosModel.setNumero(enderecoDTORequest.numero());
        enderecosModel.setComplemento(enderecoDTORequest.complemento());
        enderecosModel.setEstado(enderecoDTORequest.estado());
        enderecosModel.setUf(enderecoDTORequest.uf());

        return enderecosModel;
    }

    public EnderecoDTOResponse mapToEnderecoDTO(EnderecosModel enderecosModel){
        return new EnderecoDTOResponse(
                enderecosModel.getEndCod(),
                enderecosModel.getCidade(),
                enderecosModel.getRua(),
                enderecosModel.getBairro(),
                enderecosModel.getNumero(),
                enderecosModel.getComplemento(),
                enderecosModel.getEstado(),
                enderecosModel.getUf()
        );
    }
}
