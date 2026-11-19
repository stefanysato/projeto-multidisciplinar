package br.com.raizesdonordeste.api.service;

import br.com.raizesdonordeste.api.model.*;
import br.com.raizesdonordeste.api.repository.ContaFidelidadeRepository;
import br.com.raizesdonordeste.api.repository.MovimentacaoPontosRepository;
import br.com.raizesdonordeste.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContaFidelidadeService {

    private final ContaFidelidadeRepository contaFidelidadeRepository;
    private final MovimentacaoPontosRepository movimentacaoPontosRepository;

    public ContaFidelidadeService(
        ContaFidelidadeRepository contaFidelidadeRepository,
        MovimentacaoPontosRepository movimentacaoPontosRepository
    ) {
        this.contaFidelidadeRepository = contaFidelidadeRepository;
        this.movimentacaoPontosRepository = movimentacaoPontosRepository;
    }

    @Transactional(readOnly = true)
    public ContaFidelidade buscarPorUsuario(Long usuarioId) {

        return contaFidelidadeRepository.findByTitular_Id(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Conta de fidelidade não encontrada."
                ));

    }

    @Transactional
    public void resgatarPontos(Long usuarioId, int pontos) {
        ContaFidelidade conta = buscarPorUsuario(usuarioId);

        conta.resgatarPontos(pontos);

        MovimentacaoPontos movimentacao = new MovimentacaoPontos(
                conta,
                null,
                TipoMovimentacao.RESGATE,
                pontos
        );

        movimentacaoPontosRepository.save(movimentacao);
    }

    @Transactional
    public void creditarPontos(Pedido pedido, int pontos) {
        ContaFidelidade conta = pedido.getContaFidelidade();

        if (conta == null) { return; }

        boolean jaCreditado =
                movimentacaoPontosRepository.existsByPedido_IdAndTipo(
                        pedido.getId(),
                        TipoMovimentacao.CREDITO
                );

        if (jaCreditado) { return; }

        conta.adicionarPontos(pontos);

        MovimentacaoPontos movimentacao = new MovimentacaoPontos(
                conta,
                pedido,
                TipoMovimentacao.CREDITO,
                pontos
        );

        movimentacaoPontosRepository.save(movimentacao);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoPontos> listarMovimentacoes(Long usuarioId) {
        ContaFidelidade conta = buscarPorUsuario(usuarioId);

        return movimentacaoPontosRepository.findAllByContaFidelidade_Id(conta.getId());
    }


}
