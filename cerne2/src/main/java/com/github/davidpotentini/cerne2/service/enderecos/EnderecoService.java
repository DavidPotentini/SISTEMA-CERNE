package com.github.davidpotentini.cerne2.service.enderecos;

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
}
