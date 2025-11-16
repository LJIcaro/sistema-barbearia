package com.barbearia.controller;

import com.barbearia.dto.BloqueioDTO;
import com.barbearia.service.BloqueioService;
import com.barbearia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bloqueios")
public class BloqueioController {

    @Autowired
    private BloqueioService bloqueioService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<BloqueioDTO> criar(@Valid @RequestBody BloqueioDTO dto,
                                             Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(bloqueioService.criar(barbeiroId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BloqueioDTO> atualizar(@PathVariable Long id,
                                                 @Valid @RequestBody BloqueioDTO dto,
                                                 Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(bloqueioService.atualizar(id, barbeiroId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id,
                                        Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        bloqueioService.deletar(id, barbeiroId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/meus")
    public ResponseEntity<List<BloqueioDTO>> listarMeus(Authentication authentication) {
        String cpf = authentication.getName();
        Long barbeiroId = usuarioService.buscarPorCpf(cpf).getId();
        return ResponseEntity.ok(bloqueioService.listarPorBarbeiro(barbeiroId));
    }

    @GetMapping("/barbeiro/{barbeiroId}")
    public ResponseEntity<List<BloqueioDTO>> listarPorBarbeiro(@PathVariable Long barbeiroId) {
        return ResponseEntity.ok(bloqueioService.listarPorBarbeiro(barbeiroId));
    }
}