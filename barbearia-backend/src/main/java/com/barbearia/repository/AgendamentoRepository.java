package com.barbearia.repository;

import com.barbearia.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByClienteIdOrderByDataHoraDesc(Long clienteId);
    List<Agendamento> findByBarbeiroIdOrderByDataHoraAsc(Long barbeiroId);
    List<Agendamento> findByBarbeiroIdAndStatusOrderByDataHoraAsc(Long barbeiroId, Agendamento.StatusAgendamento status);
    
    @Query("SELECT a FROM Agendamento a WHERE a.barbeiro.id = :barbeiroId " +
           "AND a.dataHora BETWEEN :inicio AND :fim " +
           "AND a.status IN ('PENDENTE', 'CONFIRMADO')")
    List<Agendamento> findAgendamentosAtivos(@Param("barbeiroId") Long barbeiroId,
                                             @Param("inicio") LocalDateTime inicio,
                                             @Param("fim") LocalDateTime fim);
}