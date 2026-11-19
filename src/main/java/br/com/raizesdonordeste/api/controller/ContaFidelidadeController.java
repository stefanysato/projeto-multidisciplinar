package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.model.MovimentacaoPontos;
import br.com.raizesdonordeste.api.security.UsuarioDetails;
import br.com.raizesdonordeste.api.service.ContaFidelidadeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fidelidade")
public class ContaFidelidadeController {

    private final ContaFidelidadeService contaFidelidadeService;

    public ContaFidelidadeController(
            ContaFidelidadeService contaFidelidadeService
    ) {
        this.contaFidelidadeService = contaFidelidadeService;
    }

    @GetMapping("/minha-conta")
    public Map<String, Object> consultarMinhaConta(
            @AuthenticationPrincipal UsuarioDetails usuarioDetails) {
        var conta = contaFidelidadeService.buscarPorUsuario(usuarioDetails.getId());

        return Map.of(
                "id", conta.getId(),
                "saldoPontos", conta.getSaldoPontos(),
                "criadoEm", conta.getCriadoEm()
        );
    }

    @GetMapping("/minha-conta/movimentacoes")
    public List<Map<String, Object>> listarMovimentacoes(
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        List<MovimentacaoPontos> movimentacoes =
                contaFidelidadeService.listarMovimentacoes(
                        usuarioDetails.getId()
                );

        return movimentacoes.stream()
                .map(movimentacao -> {
                    Map<String, Object> dados = new HashMap<>();

                    dados.put("id", movimentacao.getId());
                    dados.put("tipo", movimentacao.getTipo());
                    dados.put("pontos", movimentacao.getPontos());
                    dados.put("criadoEm", movimentacao.getCriadoEm());

                    if (movimentacao.getPedido() != null) {
                        dados.put("pedidoId", movimentacao.getPedido().getId());
                    }

                    return dados;
                })
                .toList();
    }

    @PostMapping("/minha-conta/resgates")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resgatarPontos(
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            @RequestParam int pontos) {

        contaFidelidadeService.resgatarPontos(
                usuarioDetails.getId(),
                pontos
        );
    }

}
