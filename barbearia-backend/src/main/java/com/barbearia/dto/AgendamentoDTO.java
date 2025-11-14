package com.barbearia.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AgendamentoDTO {
    private Long id;
    
    @NotNull(message = "Barbeiro é obrigatório")
    private Long barbeiroId;
    
    @NotNull(message = "Serviço é obrigatório")
    private Long servicoId;
    
    @NotNull(message = "Data e hora são obrigatórios")
    private LocalDateTime dataHora;
    
    private String observacoes;
    private String status;
    
    // Campos para resposta
    private Long clienteId;
    private String clienteNome;
    private String barbeiroNome;
    private String servicoNome;
    private String servicoDescricao;
}