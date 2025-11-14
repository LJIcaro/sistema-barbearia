package com.barbearia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicoDTO {
    private Long id;
    
    @NotBlank(message = "Nome do serviço é obrigatório")
    private String nome;
    
    private String descricao;
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal preco;
    
    @NotNull(message = "Duração é obrigatória")
    @Min(value = 1, message = "Duração deve ser maior que zero")
    private Integer duracaoMinutos;
    
    private String materiaisNecessarios;
    private Boolean ativo;
    private Long barbeiroId;
    private String barbeiroNome;
}