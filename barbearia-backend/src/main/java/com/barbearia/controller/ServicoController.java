package com.barbearia.controller;

import com.barbearia.dto.ServicoDTO;
import com.barbearia.security.JwtUtil;
import com.barbearia.service.ServicoService;
import com.barbearia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {
    
    @Autowired
    private ServicoService servicoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping
    public ResponseEntity<ServicoDTO> criar(@Valid @RequestBody ServicoDTO dto, 
                                            Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(servicoService.criar(barbeiroId, dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ServicoDTO> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody ServicoDTO dto,
                                                Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(servicoService.atualizar(id, barbeiroId, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        servicoService.deletar(id, barbeiroId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/meus")
    public ResponseEntity<List<ServicoDTO>> listarMeus(Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(servicoService.listarPorBarbeiro(barbeiroId));
    }
    
    @GetMapping("/barbeiro/{barbeiroId}")
    public ResponseEntity<List<ServicoDTO>> listarPorBarbeiro(@PathVariable Long barbeiroId) {
        return ResponseEntity.ok(servicoService.listarPorBarbeiro(barbeiroId));
    }
    
    @GetMapping("/listar")
    public ResponseEntity<List<ServicoDTO>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ServicoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }
}