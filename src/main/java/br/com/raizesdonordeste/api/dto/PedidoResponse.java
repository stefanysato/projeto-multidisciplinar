package br.com.raizesdonordeste.api.dto;

import br.com.raizesdonordeste.api.model.CanalPedido;
import br.com.raizesdonordeste.api.model.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long unidadeId,
        Long registradoPorId,
        CanalPedido canal,
        StatusPedido status,
        BigDecimal valorTotal,
        LocalDateTime criadoEm,
        List<ItemPedidoResponse> itens
) {}
