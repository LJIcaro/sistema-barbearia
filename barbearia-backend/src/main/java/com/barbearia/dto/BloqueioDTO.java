package com.barbearia.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BloqueioDTO {
    private Long id;
    
    @NotNull(message = "Data de início é obrigatória")
    private LocalDateTime dataInicio;
    
    @NotNull(message = "Data de fim é obrigatória")
    private LocalDateTime dataFim;
    
    private String motivo;
}