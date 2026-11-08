package br.com.raizesdonordeste.api.dto;

public record LoginResponse(
    String token,
    String tipo
) {}
