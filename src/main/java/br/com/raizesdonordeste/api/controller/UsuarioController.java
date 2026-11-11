package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.dto.UsuarioRequest;
import br.com.raizesdonordeste.api.dto.UsuarioResponse;
import br.com.raizesdonordeste.api.security.UsuarioDetails;
import br.com.raizesdonordeste.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(
            @Valid @RequestBody UsuarioRequest request) {

        UsuarioResponse usuario = service.cadastrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));

    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponse> buscarMeuPerfil(
            @AuthenticationPrincipal UsuarioDetails usuarioDetails) {

        return ResponseEntity.ok(
                service.buscarPorId(usuarioDetails.getId())
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativarUsuario(@PathVariable Long id) {
        service.ativarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarUsuario(@PathVariable Long id) {
        service.desativarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    
}
