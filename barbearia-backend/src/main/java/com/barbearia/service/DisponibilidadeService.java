package com.barbearia.service;

import com.barbearia.dto.DisponibilidadeDTO;
import com.barbearia.model.Disponibilidade;
import com.barbearia.model.Usuario;
import com.barbearia.repository.DisponibilidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DisponibilidadeService {
    
    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Transactional
    public DisponibilidadeDTO criar(Long barbeiroId, DisponibilidadeDTO dto) {
        Usuario barbeiro = usuarioService.buscarPorId(barbeiroId);
        
        if (barbeiro.getTipoUsuario() != Usuario.TipoUsuario.BARBEIRO) {
            throw new RuntimeException("Apenas barbeiros podem definir disponibilidade");
        }
        
        Disponibilidade disponibilidade = new Disponibilidade();
        disponibilidade.setBarbeiro(barbeiro);
        disponibilidade.setDiaSemana(dto.getDiaSemana());
        disponibilidade.setHoraInicio(dto.getHoraInicio());
        disponibilidade.setHoraFim(dto.getHoraFim());
        disponibilidade.setAtivo(true);
        
        disponibilidade = disponibilidadeRepository.save(disponibilidade);
        return toDTO(disponibilidade);
    }
    
    @Transactional
    public DisponibilidadeDTO atualizar(Long id, Long barbeiroId, DisponibilidadeDTO dto) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilidade não encontrada"));
        
        if (!disponibilidade.getBarbeiro().getId().equals(barbeiroId)) {
            throw new RuntimeException("Você não tem permissão para editar esta disponibilidade");
        }
        
        disponibilidade.setDiaSemana(dto.getDiaSemana());
        disponibilidade.setHoraInicio(dto.getHoraInicio());
        disponibilidade.setHoraFim(dto.getHoraFim());
        if (dto.getAtivo() != null) {
            disponibilidade.setAtivo(dto.getAtivo());
        }
        
        disponibilidade = disponibilidadeRepository.save(disponibilidade);
        return toDTO(disponibilidade);
    }
    
    @Transactional
    public void deletar(Long id, Long barbeiroId) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilidade não encontrada"));
        
        if (!disponibilidade.getBarbeiro().getId().equals(barbeiroId)) {
            throw new RuntimeException("Você não tem permissão para deletar esta disponibilidade");
        }
        
        disponibilidadeRepository.delete(disponibilidade);
    }
    
    public List<DisponibilidadeDTO> listarPorBarbeiro(Long barbeiroId) {
        return disponibilidadeRepository.findByBarbeiroIdAndAtivoTrue(barbeiroId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private DisponibilidadeDTO toDTO(Disponibilidade disponibilidade) {
        DisponibilidadeDTO dto = new DisponibilidadeDTO();
        dto.setId(disponibilidade.getId());
        dto.setDiaSemana(disponibilidade.getDiaSemana());
        dto.setHoraInicio(disponibilidade.getHoraInicio());
        dto.setHoraFim(disponibilidade.getHoraFim());
        dto.setAtivo(disponibilidade.getAtivo());
        return dto;
    }
}