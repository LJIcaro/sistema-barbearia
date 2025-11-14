package com.barbearia.repository;

import com.barbearia.model.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Long> {
    List<Disponibilidade> findByBarbeiroIdAndAtivoTrue(Long barbeiroId);
    List<Disponibilidade> findByBarbeiroIdAndDiaSemanaAndAtivoTrue(Long barbeiroId, Integer diaSemana);
}