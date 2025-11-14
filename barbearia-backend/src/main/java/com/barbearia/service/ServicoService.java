package com.barbearia.service;

import com.barbearia.dto.ServicoDTO;
import com.barbearia.model.Servico;
import com.barbearia.model.Usuario;
import com.barbearia.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicoService {
    
    @Autowired
    private ServicoRepository servicoRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Transactional
    public ServicoDTO criar(Long barbeiroId, ServicoDTO dto) {
        Usuario barbeiro = usuarioService.buscarPorId(barbeiroId);
        
        if (barbeiro.getTipoUsuario() != Usuario.TipoUsuario.BARBEIRO) {
            throw new RuntimeException("Apenas barbeiros podem criar serviços");
        }
        
        if (servicoRepository.existsByBarbeiroIdAndNome(barbeiroId, dto.getNome())) {
            throw new RuntimeException("Você já possui um serviço com este nome");
        }
        
        Servico servico = new Servico();
        servico.setBarbeiro(barbeiro);
        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setPreco(dto.getPreco());
        servico.setDuracaoMinutos(dto.getDuracaoMinutos());
        servico.setMateriaisNecessarios(dto.getMateriaisNecessarios());
        servico.setAtivo(true);
        
        servico = servicoRepository.save(servico);
        return toDTO(servico);
    }
    
    @Transactional
    public ServicoDTO atualizar(Long id, Long barbeiroId, ServicoDTO dto) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        
        if (!servico.getBarbeiro().getId().equals(barbeiroId)) {
            throw new RuntimeException("Você não tem permissão para editar este serviço");
        }
        
        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setPreco(dto.getPreco());
        servico.setDuracaoMinutos(dto.getDuracaoMinutos());
        servico.setMateriaisNecessarios(dto.getMateriaisNecessarios());
        if (dto.getAtivo() != null) {
            servico.setAtivo(dto.getAtivo());
        }
        
        servico = servicoRepository.save(servico);
        return toDTO(servico);
    }
    
    @Transactional
    public void deletar(Long id, Long barbeiroId) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        
        if (!servico.getBarbeiro().getId().equals(barbeiroId)) {
            throw new RuntimeException("Você não tem permissão para deletar este serviço");
        }
        
        servicoRepository.delete(servico);
    }
    
    public List<ServicoDTO> listarPorBarbeiro(Long barbeiroId) {
        return servicoRepository.findByBarbeiroIdAndAtivoTrue(barbeiroId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<ServicoDTO> listarTodos() {
        return servicoRepository.findByAtivoTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public ServicoDTO buscarPorId(Long id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        return toDTO(servico);
    }
    
    private ServicoDTO toDTO(Servico servico) {
        ServicoDTO dto = new ServicoDTO();
        dto.setId(servico.getId());
        dto.setNome(servico.getNome());
        dto.setDescricao(servico.getDescricao());
        dto.setPreco(servico.getPreco());
        dto.setDuracaoMinutos(servico.getDuracaoMinutos());
        dto.setMateriaisNecessarios(servico.getMateriaisNecessarios());
        dto.setAtivo(servico.getAtivo());
        dto.setBarbeiroId(servico.getBarbeiro().getId());
        dto.setBarbeiroNome(servico.getBarbeiro().getNomeCompleto());
        return dto;
    }
}