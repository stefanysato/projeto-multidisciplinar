package br.com.raizesdonordeste.api.dto;

import br.com.raizesdonordeste.api.model.CategoriaProduto;

import java.time.LocalDateTime;

public record ProdutoResponse(
        Long id,
        String nome,
        String descricao,
        CategoriaProduto categoria,
        boolean ativo,
        LocalDateTime criadoEm
) {
}
