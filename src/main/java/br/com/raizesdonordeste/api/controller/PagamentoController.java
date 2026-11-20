package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.model.Pagamento;
import br.com.raizesdonordeste.api.model.StatusPagamento;
import br.com.raizesdonordeste.api.service.PagamentoService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pedidos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/{pedidoId}/pagamentos")
    public Map<String, Object> processarPagamento(
            @PathVariable Long pedidoId) {

        Pagamento pagamento = pagamentoService.processarPagamento(
                pedidoId
        );

        String mensagem = pagamento.getStatus() == StatusPagamento.APROVADO
                ? "Pagamento aprovado."
                : "Pagamento recusado.";

        return Map.of(
                "pagamentoId", pagamento.getId(),
                "pedidoId", pagamento.getPedido().getId(),
                "valor", pagamento.getValor(),
                "status", pagamento.getStatus(),
                "mensagem", mensagem,
                "criadoEm", pagamento.getCriadoEm()
        );
    }


    @GetMapping("/{pedidoId}/pagamentos")
    public java.util.List<Map<String, Object>> listarPagamentos(
            @PathVariable Long pedidoId) {

        return pagamentoService.listarPagamentosPedido(
                pedidoId
        );
    }

}
