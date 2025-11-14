package com.barbearia.repository;

import com.barbearia.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findByBarbeiroIdAndAtivoTrue(Long barbeiroId);
    List<Servico> findByAtivoTrue();
    boolean existsByBarbeiroIdAndNome(Long barbeiroId, String nome);
}