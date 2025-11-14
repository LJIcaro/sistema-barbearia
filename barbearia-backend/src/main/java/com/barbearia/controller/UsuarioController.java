package com.barbearia.controller;

import com.barbearia.model.Usuario;
import com.barbearia.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUsuarioLogado(Authentication authentication) {
        String cpf = authentication.getName();
        Usuario usuario = usuarioService.buscarPorCpf(cpf);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("nomeCompleto", usuario.getNomeCompleto());
        response.put("cpf", usuario.getCpf());
        response.put("tipoUsuario", usuario.getTipoUsuario().name());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/barbeiros/listar")
    public ResponseEntity<List<Map<String, Object>>> listarBarbeiros() {
        List<Usuario> barbeiros = usuarioService.listarBarbeiros();
        
        List<Map<String, Object>> response = barbeiros.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("nomeCompleto", b.getNomeCompleto());
            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
}