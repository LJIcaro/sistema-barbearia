package com.barbearia.controller;

import com.barbearia.dto.DisponibilidadeDTO;
import com.barbearia.service.DisponibilidadeService;
import com.barbearia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadeController {
    
    @Autowired
    private DisponibilidadeService disponibilidadeService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping
    public ResponseEntity<DisponibilidadeDTO> criar(@Valid @RequestBody DisponibilidadeDTO dto,
                                                    Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(disponibilidadeService.criar(barbeiroId, dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadeDTO> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody DisponibilidadeDTO dto,
                                                        Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(disponibilidadeService.atualizar(id, barbeiroId, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        disponibilidadeService.deletar(id, barbeiroId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/minhas")
    public ResponseEntity<List<DisponibilidadeDTO>> listarMinhas(Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(disponibilidadeService.listarPorBarbeiro(barbeiroId));
    }
    
    @GetMapping("/barbeiro/{barbeiroId}")
    public ResponseEntity<List<DisponibilidadeDTO>> listarPorBarbeiro(@PathVariable Long barbeiroId) {
        return ResponseEntity.ok(disponibilidadeService.listarPorBarbeiro(barbeiroId));
    }
}