package com.barbearia.service;

import com.barbearia.dto.AgendamentoDTO;
import com.barbearia.dto.HorarioDisponivelDTO;
import com.barbearia.model.*;
import com.barbearia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {
    
    @Autowired
    private AgendamentoRepository agendamentoRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ServicoRepository servicoRepository;
    
    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;
    
    @Autowired
    private BloqueioRepository bloqueioRepository;
    
    @Transactional
    public AgendamentoDTO criar(Long clienteId, AgendamentoDTO dto) {
        Usuario cliente = usuarioService.buscarPorId(clienteId);
        Usuario barbeiro = usuarioService.buscarPorId(dto.getBarbeiroId());
        Servico servico = servicoRepository.findById(dto.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        
        // Validar se o horário está disponível
        if (!isHorarioDisponivel(dto.getBarbeiroId(), dto.getDataHora(), servico.getDuracaoMinutos())) {
            throw new RuntimeException("Horário não disponível");
        }
        
        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setBarbeiro(barbeiro);
        agendamento.setServico(servico);
        agendamento.setDataHora(dto.getDataHora());
        agendamento.setObservacoes(dto.getObservacoes());
        agendamento.setStatus(Agendamento.StatusAgendamento.PENDENTE);
        
        agendamento = agendamentoRepository.save(agendamento);
        return toDTO(agendamento);
    }
    
    @Transactional
    public AgendamentoDTO atualizarStatus(Long id, String status, Long usuarioId) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        
        // Validar permissão
        if (!agendamento.getCliente().getId().equals(usuarioId) && 
            !agendamento.getBarbeiro().getId().equals(usuarioId)) {
            throw new RuntimeException("Você não tem permissão para alterar este agendamento");
        }
        
        agendamento.setStatus(Agendamento.StatusAgendamento.valueOf(status.toUpperCase()));
        agendamento = agendamentoRepository.save(agendamento);
        return toDTO(agendamento);
    }
    
    public List<AgendamentoDTO> listarPorCliente(Long clienteId) {
        return agendamentoRepository.findByClienteIdOrderByDataHoraDesc(clienteId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<AgendamentoDTO> listarPorBarbeiro(Long barbeiroId) {
        return agendamentoRepository.findByBarbeiroIdOrderByDataHoraAsc(barbeiroId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<HorarioDisponivelDTO> listarHorariosDisponiveis(Long barbeiroId, LocalDate data) {
        List<HorarioDisponivelDTO> horarios = new ArrayList<>();
        
        // Buscar disponibilidade do barbeiro para o dia da semana
        int diaSemana = data.getDayOfWeek().getValue() % 7; // Converter para 0-6
        List<Disponibilidade> disponibilidades = disponibilidadeRepository
                .findByBarbeiroIdAndDiaSemanaAndAtivoTrue(barbeiroId, diaSemana);
        
        if (disponibilidades.isEmpty()) {
            return horarios;
        }
        
        for (Disponibilidade disp : disponibilidades) {
            LocalTime horaAtual = disp.getHoraInicio();
            
            while (horaAtual.isBefore(disp.getHoraFim())) {
                LocalDateTime dataHora = LocalDateTime.of(data, horaAtual);
                boolean disponivel = isHorarioDisponivel(barbeiroId, dataHora, 30); // 30 min padrão
                horarios.add(new HorarioDisponivelDTO(dataHora, disponivel));
                horaAtual = horaAtual.plusMinutes(30);
            }
        }
        
        return horarios;
    }
    
    private boolean isHorarioDisponivel(Long barbeiroId, LocalDateTime dataHora, Integer duracaoMinutos) {
        // Verificar se está no passado
        if (dataHora.isBefore(LocalDateTime.now())) {
            return false;
        }
        
        // Verificar bloqueios
        List<Bloqueio> bloqueios = bloqueioRepository.findBloqueiosAtivos(barbeiroId, dataHora);
        if (!bloqueios.isEmpty()) {
            return false;
        }
        
        // Verificar agendamentos existentes
        LocalDateTime fim = dataHora.plusMinutes(duracaoMinutos);
        List<Agendamento> agendamentos = agendamentoRepository.findAgendamentosAtivos(
                barbeiroId, dataHora.minusMinutes(duracaoMinutos), fim);
        
        return agendamentos.isEmpty();
    }
    
    private AgendamentoDTO toDTO(Agendamento agendamento) {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setId(agendamento.getId());
        dto.setClienteId(agendamento.getCliente().getId());
        dto.setClienteNome(agendamento.getCliente().getNomeCompleto());
        dto.setBarbeiroId(agendamento.getBarbeiro().getId());
        dto.setBarbeiroNome(agendamento.getBarbeiro().getNomeCompleto());
        dto.setServicoId(agendamento.getServico().getId());
        dto.setServicoNome(agendamento.getServico().getNome());
        dto.setServicoDescricao(agendamento.getServico().getDescricao());
        dto.setDataHora(agendamento.getDataHora());
        dto.setStatus(agendamento.getStatus().name());
        dto.setObservacoes(agendamento.getObservacoes());
        return dto;
    }
}