package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.dto.UnidadeRequest;
import br.com.raizesdonordeste.api.dto.UnidadeResponse;
import br.com.raizesdonordeste.api.model.Unidade;
import br.com.raizesdonordeste.api.service.UnidadeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {

    private final UnidadeService service;

    public UnidadeController(UnidadeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UnidadeResponse> cadastrar(
            @Valid @RequestBody UnidadeRequest request
    ) {
        UnidadeResponse unidade = service.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(unidade);
    }

    @GetMapping
    public ResponseEntity<List<UnidadeResponse>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadeResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        service.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadeResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UnidadeRequest request
    ) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }
}
