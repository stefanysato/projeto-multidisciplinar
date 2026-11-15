package br.com.raizesdonordeste.api.repository;

import br.com.raizesdonordeste.api.model.EstoqueUnidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstoqueUnidadeRepository extends JpaRepository<EstoqueUnidade, Long> {

    // busca o estoque de um produto em uma unidade específica
    Optional<EstoqueUnidade> findByProduto_IdAndUnidade_Id(
        Long produtoId, Long unidadeId
    );

    // lista todos os registros de estoque de uma unidade
    List<EstoqueUnidade> findAllByUnidade_Id(Long unidadeId);

    // verifica se já existe um registro para determinada combinação
    boolean existsByProduto_IdAndUnidade_Id(Long produtoId, Long unidadeId);
}
