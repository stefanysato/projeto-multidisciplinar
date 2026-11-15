package br.com.raizesdonordeste.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EstoqueUnidadeRequest(

        @NotNull
        @Positive
        Long produtoId,

        @NotNull
        @Positive
        Long unidadeId,

        @NotNull
        @PositiveOrZero
        Integer quantidade,

        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 8, fraction = 2)
        BigDecimal preco

) {}
