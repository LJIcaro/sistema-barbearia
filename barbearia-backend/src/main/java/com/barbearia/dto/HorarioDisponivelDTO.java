package com.barbearia.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HorarioDisponivelDTO {
    private LocalDateTime dataHora;
    private boolean disponivel;
}