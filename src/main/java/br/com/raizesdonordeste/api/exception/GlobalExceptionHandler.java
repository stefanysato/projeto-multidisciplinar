package br.com.raizesdonordeste.api.exception;

import br.com.raizesdonordeste.api.dto.ErroResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Erros HTTP
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> tratarResponseStatusException(
            ResponseStatusException ex) {

        int status = ex.getStatusCode().value();

        HttpStatus httpStatus = HttpStatus.resolve(status);

        String erro = httpStatus != null
                ? httpStatus.getReasonPhrase()
                : "Erro HTTP";

        ErroResponse resposta = new ErroResponse(
                status,
                erro,
                ex.getReason() != null ? ex.getReason() : "Ocorreu um erro",
                null
        );

        return ResponseEntity.status(status).body(resposta);
    }

    // Erros de validação dos DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarErrosDeValidacao(
            MethodArgumentNotValidException ex) {

        List<String> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

        ErroResponse resposta = new ErroResponse(
            400,
            "Bad Request",
            "Existem campos inválidos",
            detalhes
        );

        return ResponseEntity
                .badRequest()
                .body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInterno(Exception ex) {

        log.error("Erro inesperado na aplicação", ex);

        ErroResponse resposta = new ErroResponse(
          500,
          "Internal Server Error",
          "Ocorreu um erro interno. Tente novamente mais tarde.",
          null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(resposta);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> tratarFalhaAutenticacao(
            AuthenticationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", "E-mail ou senha inválidos"));
    }

}