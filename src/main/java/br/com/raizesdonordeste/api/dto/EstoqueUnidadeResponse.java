package br.com.raizesdonordeste.api.dto;

import br.com.raizesdonordeste.api.model.CategoriaProduto;

import java.math.BigDecimal;

public record EstoqueUnidadeResponse(
        Long id,
        Long produtoId,
        String produtoNome,
        CategoriaProduto categoria,
        Long unidadeId,
        String unidadeNome,
        Integer quantidade,
        BigDecimal preco
) {}
