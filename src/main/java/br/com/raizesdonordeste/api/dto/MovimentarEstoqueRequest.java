package br.com.raizesdonordeste.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimentarEstoqueRequest(

        @NotNull
        @Positive
        Integer quantidade

) {}
