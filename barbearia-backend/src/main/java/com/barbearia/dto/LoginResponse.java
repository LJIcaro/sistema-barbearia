package com.barbearia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo;
    private Long userId;
    private String nomeCompleto;
    private String tipoUsuario;
}