package br.com.raizesdonordeste.api.controller;


import br.com.raizesdonordeste.api.dto.AtualizarPrecoRequest;
import br.com.raizesdonordeste.api.dto.EstoqueUnidadeRequest;
import br.com.raizesdonordeste.api.dto.EstoqueUnidadeResponse;
import br.com.raizesdonordeste.api.dto.MovimentarEstoqueRequest;
import br.com.raizesdonordeste.api.service.EstoqueUnidadeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class EstoqueUnidadeController {

    private final EstoqueUnidadeService estoqueService;

    public EstoqueUnidadeController(EstoqueUnidadeService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping("/estoque")
    public ResponseEntity<EstoqueUnidadeResponse> cadastrar(
            @Valid @RequestBody EstoqueUnidadeRequest request
    ) {
        EstoqueUnidadeResponse response = estoqueService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/unidades/{unidadeId}/estoque")
    public ResponseEntity<List<EstoqueUnidadeResponse>> listarPorUnidade(
            @PathVariable Long unidadeId
    ) {
        return ResponseEntity.ok(estoqueService.listarPorUnidade(unidadeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueUnidadeResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(estoqueService.buscarPorId(id));
    }

    @PatchMapping("/{id}/preco")
    public ResponseEntity<EstoqueUnidadeResponse> atualizarPreco(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarPrecoRequest request
    ) {
        return ResponseEntity.ok(estoqueService.atualizarPreco(id, request));
    }

    @PatchMapping("/{id}/entrada")
    public ResponseEntity<EstoqueUnidadeResponse> registrarEntrada(
            @PathVariable Long id,
            @Valid @RequestBody MovimentarEstoqueRequest request
            ) {
        return ResponseEntity.ok(estoqueService.registrarEntrada(id, request));
    }

    @PatchMapping("/{id}/saida")
    public ResponseEntity<EstoqueUnidadeResponse> registrarSaida(
            @PathVariable Long id,
            @Valid @RequestBody MovimentarEstoqueRequest request
    ) {
        return ResponseEntity.ok(estoqueService.registrarSaida(id, request));
    }
}
