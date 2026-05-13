package com.github.davidpotentini.cerne2.service.formulario;

import com.github.davidpotentini.cerne2.dto.formulario.FormularioDTO;
import com.github.davidpotentini.cerne2.mapper.formularios.FormularioMapper;
import com.github.davidpotentini.cerne2.repository.formulario.FormularioRepository;
import com.github.davidpotentini.cerne2.repository.formulario.FormularioRespostasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FormularioService {

    private final FormularioRepository formularioRepository;
    private final FormularioRespostasRepository formularioRespostasRepository;
    private final FormularioMapper formularioMapper;

    public FormularioService(FormularioRepository formularioRepository, FormularioRespostasRepository formularioRespostasRepository, FormularioMapper formularioMapper) {
        this.formularioRepository = formularioRepository;
        this.formularioRespostasRepository = formularioRespostasRepository;
        this.formularioMapper = formularioMapper;
    }

    public List<FormularioDTO> findAll(){
        return formularioMapper.toDTOList(formularioRepository.findAll());
    }

    public FormularioDTO findById(Long frmCod){
        return formularioMapper.toDTO(formularioRepository
                .findById(frmCod).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional(rollbackFor = Exception.class)
    public FormularioDTO save(FormularioDTO formularioDTO, Long frmCod){
        return formularioMapper.toDTO(formularioRepository.save(formularioMapper.toModel(formularioDTO, frmCod)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long frmCod){
        formularioRepository.deleteById(frmCod);
    }
}
