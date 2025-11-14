package com.barbearia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class DisponibilidadeDTO {
    private Long id;
    
    @NotNull(message = "Dia da semana é obrigatório")
    @Min(value = 0, message = "Dia da semana deve ser entre 0 e 6")
    @Max(value = 6, message = "Dia da semana deve ser entre 0 e 6")
    private Integer diaSemana;
    
    @NotNull(message = "Hora de início é obrigatória")
    private LocalTime horaInicio;
    
    @NotNull(message = "Hora de fim é obrigatória")
    private LocalTime horaFim;
    
    private Boolean ativo;
}