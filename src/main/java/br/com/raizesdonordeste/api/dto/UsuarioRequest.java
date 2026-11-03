package br.com.raizesdonordeste.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max=120)
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max=180)
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min=8, max=72, message = "A senha deve ter entre 8 e 72 caracteres")
        String senha

) {}