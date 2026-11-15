package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.dto.PedidoRequest;
import br.com.raizesdonordeste.api.dto.PedidoResponse;
import br.com.raizesdonordeste.api.model.StatusPedido;
import br.com.raizesdonordeste.api.model.Usuario;
import br.com.raizesdonordeste.api.security.UsuarioDetails;
import br.com.raizesdonordeste.api.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> registrarPedido(
            @Valid @RequestBody PedidoRequest request,
            @AuthenticationPrincipal UsuarioDetails usuario
    ) {
        PedidoResponse response = pedidoService.registrarPedido(
                request,
                usuario.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        return ResponseEntity.ok(
                pedidoService.listarTodos()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusPedido status
    ) {
        return ResponseEntity.ok(
                pedidoService.atualizarStatus(id, status)
        );
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelarPedido(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                pedidoService.cancelarPedido(id)
        );
    }

}
