package br.com.raizesdonordeste.api.repository;

import br.com.raizesdonordeste.api.model.MovimentacaoPontos;
import br.com.raizesdonordeste.api.model.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoPontosRepository extends JpaRepository<MovimentacaoPontos, Long> {

    List<MovimentacaoPontos> findAllByContaFidelidade_Id (Long contaId);

    List<MovimentacaoPontos> findAllByPedido_Id (Long id);

    boolean existsByPedido_IdAndTipo (Long pedidoId, TipoMovimentacao tipo);

}
