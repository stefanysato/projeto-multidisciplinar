package br.com.raizesdonordeste.api.repository;

import br.com.raizesdonordeste.api.model.Pagamento;
import br.com.raizesdonordeste.api.model.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findAllByPedido_IdOrderByCriadoEmDesc(Long pedidoId);
    boolean existsByPedido_IdAndStatus(Long pedidoId, StatusPagamento status);
}