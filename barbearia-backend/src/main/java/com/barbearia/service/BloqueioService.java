package com.barbearia.service;

import com.barbearia.dto.BloqueioDTO;
import com.barbearia.model.Bloqueio;
import com.barbearia.model.Usuario;
import com.barbearia.repository.BloqueioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloqueioService {

    @Autowired
    private BloqueioRepository bloqueioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Transactional
    public BloqueioDTO criar(Long barbeiroId, BloqueioDTO dto) {
        Usuario barbeiro = usuarioService.buscarPorId(barbeiroId);

        if (barbeiro.getTipoUsuario() != Usuario.TipoUsuario.BARBEIRO) {
            throw new RuntimeException("Apenas barbeiros podem criar bloqueios de horário");
        }

        Bloqueio bloqueio = new Bloqueio();
        bloqueio.setBarbeiro(barbeiro);
        bloqueio.setDataInicio(dto.getDataInicio());
        bloqueio.setDataFim(dto.getDataFim());
        bloqueio.setMotivo(dto.getMotivo());

        bloqueio = bloqueioRepository.save(bloqueio);
        return toDTO(bloqueio);
    }

    @Transactional
    public BloqueioDTO atualizar(Long id, Long barbeiroId, BloqueioDTO dto) {
        Bloqueio bloqueio = bloqueioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bloqueio não encontrado"));

        if (!bloqueio.getBarbeiro().getId().equals(barbeiroId)) {
            throw new RuntimeException("Você não tem permissão para editar este bloqueio");
        }

        bloqueio.setDataInicio(dto.getDataInicio());
        bloqueio.setDataFim(dto.getDataFim());
        bloqueio.setMotivo(dto.getMotivo());

        bloqueio = bloqueioRepository.save(bloqueio);
        return toDTO(bloqueio);
    }

    @Transactional
    public void deletar(Long id, Long barbeiroId) {
        Bloqueio bloqueio = bloqueioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bloqueio não encontrado"));

        if (!bloqueio.getBarbeiro().getId().equals(barbeiroId)) {
            throw new RuntimeException("Você não tem permissão para excluir este bloqueio");
        }

        bloqueioRepository.delete(bloqueio);
    }

    public List<BloqueioDTO> listarPorBarbeiro(Long barbeiroId) {
        return bloqueioRepository.findByBarbeiroId(barbeiroId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private BloqueioDTO toDTO(Bloqueio bloqueio) {
        BloqueioDTO dto = new BloqueioDTO();
        dto.setId(bloqueio.getId());
        dto.setDataInicio(bloqueio.getDataInicio());
        dto.setDataFim(bloqueio.getDataFim());
        dto.setMotivo(bloqueio.getMotivo());
        return dto;
    }
}