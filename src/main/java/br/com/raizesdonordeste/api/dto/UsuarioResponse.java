package br.com.raizesdonordeste.api.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        boolean ativo,
        LocalDateTime criadoEm
) {
}
