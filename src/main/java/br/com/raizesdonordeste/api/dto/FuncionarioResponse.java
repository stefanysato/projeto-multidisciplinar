package br.com.raizesdonordeste.api.dto;

import br.com.raizesdonordeste.api.model.Role;

import java.time.LocalDateTime;

public record FuncionarioResponse(
        Long id,
        String nome,
        String email,
        Role role,
        Long unidadeId,
        String unidadeNome,
        boolean ativo,
        LocalDateTime criadoEm
) {}
