package com.barbearia.repository;

import com.barbearia.model.Bloqueio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BloqueioRepository extends JpaRepository<Bloqueio, Long> {
    List<Bloqueio> findByBarbeiroId(Long barbeiroId);
    
    @Query("SELECT b FROM Bloqueio b WHERE b.barbeiro.id = :barbeiroId " +
           "AND b.dataInicio <= :dataHora AND b.dataFim >= :dataHora")
    List<Bloqueio> findBloqueiosAtivos(@Param("barbeiroId") Long barbeiroId, 
                                       @Param("dataHora") LocalDateTime dataHora);
}