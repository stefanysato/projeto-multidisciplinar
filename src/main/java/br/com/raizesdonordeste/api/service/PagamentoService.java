
package br.com.raizesdonordeste.api.service;

import br.com.raizesdonordeste.api.model.Pedido;
import br.com.raizesdonordeste.api.model.Pagamento;
import br.com.raizesdonordeste.api.model.StatusPagamento;
import br.com.raizesdonordeste.api.repository.PedidoRepository;
import br.com.raizesdonordeste.api.repository.PagamentoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoMockService pagamentoMockService;
    private final ContaFidelidadeService contaFidelidadeService;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository,
            PagamentoMockService pagamentoMockService,
            ContaFidelidadeService contaFidelidadeService
    ) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.pagamentoMockService = pagamentoMockService;
        this.contaFidelidadeService = contaFidelidadeService;
    }

    @Transactional
    public Pagamento processarPagamento(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido não encontrado."
                ));

        // impede pagamento duplicado de pedidos aprovados
        if (pagamentoRepository.existsByPedido_IdAndStatus(
                pedidoId, StatusPagamento.APROVADO)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este pedido já possui um pagamento aprovado"
            );
        }

        Pagamento pagamento = new Pagamento(
                pedido,
                pedido.getValorTotal()
        );

        pagamentoRepository.save(pagamento);

        // simulação do pagamento
        boolean autorizado = pagamentoMockService.autorizar(
                pagamento.getValor()
        );

        if (autorizado) {
            pagamento.aprovar();

            // 1 ponto por real gasto
            int pontos = calcularPontos(pagamento.getValor());

            if (pontos > 0) {
                contaFidelidadeService.creditarPontos(pedido, pontos);
            }
        } else {
            pagamento.recusar();
        }
        return pagamento;
    }


    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarPagamentosPedido(
            Long pedidoId) {

        return pagamentoRepository
                .findAllByPedido_IdOrderByCriadoEmDesc(pedidoId)
                .stream()
                .map(pagamento -> Map.<String, Object>of(
                        "pagamentoId", pagamento.getId(),
                        "pedidoId", pedidoId,
                        "valor", pagamento.getValor(),
                        "status", pagamento.getStatus(),
                        "criadoEm", pagamento.getCriadoEm()
                ))
                .toList();
    }


    private int calcularPontos(BigDecimal valor) {
        return valor
                .setScale(0, RoundingMode.DOWN)
                .intValueExact();
    }
}
