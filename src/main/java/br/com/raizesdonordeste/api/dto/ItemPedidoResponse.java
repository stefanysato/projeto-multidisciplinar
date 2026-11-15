package br.com.raizesdonordeste.api.dto;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        Long produtoId,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {}
