package com.barbearia.service;

import com.barbearia.dto.CadastroRequest;
import com.barbearia.dto.LoginRequest;
import com.barbearia.dto.LoginResponse;
import com.barbearia.model.Usuario;
import com.barbearia.repository.UsuarioRepository;
import com.barbearia.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Transactional
    public LoginResponse cadastrar(CadastroRequest request) {
        // Validar CPF
        if (usuarioRepository.existsByCpf(request.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }
        
        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(request.getNomeCompleto());
        usuario.setCpf(request.getCpf());
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(request.getTipoUsuario().toUpperCase()));
        usuario.setAtivo(true);
        
        usuario = usuarioRepository.save(usuario);
        
        // Gerar token
        String token = jwtUtil.generateToken(
            usuario.getCpf(), 
            usuario.getId(), 
            usuario.getTipoUsuario().name()
        );
        
        return new LoginResponse(
            token, 
            "Bearer", 
            usuario.getId(), 
            usuario.getNomeCompleto(), 
            usuario.getTipoUsuario().name()
        );
    }
    
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCpf(request.getCpf())
                .orElseThrow(() -> new RuntimeException("CPF ou senha inválidos"));
        
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            throw new RuntimeException("CPF ou senha inválidos");
        }
        
        if (!usuario.getAtivo()) {
            throw new RuntimeException("Usuário inativo");
        }
        
        String token = jwtUtil.generateToken(
            usuario.getCpf(), 
            usuario.getId(), 
            usuario.getTipoUsuario().name()
        );
        
        return new LoginResponse(
            token, 
            "Bearer", 
            usuario.getId(), 
            usuario.getNomeCompleto(), 
            usuario.getTipoUsuario().name()
        );
    }
}