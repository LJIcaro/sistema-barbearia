package com.barbearia.controller;

import com.barbearia.dto.AgendamentoDTO;
import com.barbearia.dto.HorarioDisponivelDTO;
import com.barbearia.service.AgendamentoService;
import com.barbearia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {
    
    @Autowired
    private AgendamentoService agendamentoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping
    public ResponseEntity<AgendamentoDTO> criar(@Valid @RequestBody AgendamentoDTO dto,
                                                Authentication authentication) {
        String cpf = authentication.getName();
        Long clienteId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(agendamentoService.criar(clienteId, dto));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<AgendamentoDTO> atualizarStatus(@PathVariable Long id,
                                                          @RequestBody Map<String, String> body,
                                                          Authentication authentication) {
        String cpf = authentication.getName();
        Long usuarioId = usuarioService.buscarPorCpf(cpf).getId();
        String status = body.get("status");
        return ResponseEntity.ok(agendamentoService.atualizarStatus(id, status, usuarioId));
    }
    
    @GetMapping("/meus")
    public ResponseEntity<List<AgendamentoDTO>> listarMeus(Authentication authentication) {
        String cpf = authentication.getName();
        Long clienteId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(agendamentoService.listarPorCliente(clienteId));
    }
    
    @GetMapping("/barbeiro")
    public ResponseEntity<List<AgendamentoDTO>> listarDoBarbeiro(Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(agendamentoService.listarPorBarbeiro(barbeiroId));
    }
    
    @GetMapping("/horarios-disponiveis")
    public ResponseEntity<List<HorarioDisponivelDTO>> listarHorariosDisponiveis(
            @RequestParam Long barbeiroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(agendamentoService.listarHorariosDisponiveis(barbeiroId, data));
    }
}