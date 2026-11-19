package br.com.raizesdonordeste.api.repository;

import br.com.raizesdonordeste.api.model.ContaFidelidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContaFidelidadeRepository extends JpaRepository<ContaFidelidade, Long> {
    Optional<ContaFidelidade> findByTitular_Id(Long usuarioId);

    boolean existsByTitular_Id(Long usuarioId);
}
