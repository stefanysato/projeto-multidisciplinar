package br.com.raizesdonordeste.api.dto;

import br.com.raizesdonordeste.api.model.CanalPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PedidoRequest(

        @NotNull
        @Positive
        Long unidadeId,

        @NotNull
        CanalPedido canal,

        @NotEmpty
        List<@Valid ItemPedidoRequest> itens

) {
}
