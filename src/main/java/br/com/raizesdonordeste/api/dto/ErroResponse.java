package br.com.raizesdonordeste.api.dto;

import java.util.List;

public record ErroResponse(
        int status,
        String erro,
        String mensagem,
        List<String> detalhes
) {
}
