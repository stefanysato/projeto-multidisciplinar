package br.com.raizesdonordeste.api.service;

import br.com.raizesdonordeste.api.dto.ItemPedidoResponse;
import br.com.raizesdonordeste.api.dto.PedidoRequest;
import br.com.raizesdonordeste.api.dto.PedidoResponse;
import br.com.raizesdonordeste.api.model.*;
import br.com.raizesdonordeste.api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UnidadeRepository unidadeRepository;
    private final EstoqueUnidadeRepository estoqueUnidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContaFidelidadeRepository  contaFidelidadeRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            UnidadeRepository unidadeRepository,
            EstoqueUnidadeRepository estoqueUnidadeRepository,
            UsuarioRepository usuarioRepository,
            ContaFidelidadeRepository contaFidelidadeRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.unidadeRepository = unidadeRepository;
        this.estoqueUnidadeRepository = estoqueUnidadeRepository;
        this.usuarioRepository =  usuarioRepository;
        this.contaFidelidadeRepository = contaFidelidadeRepository;
    }


    @Transactional
    public PedidoResponse registrarPedido(
            PedidoRequest request,
            Long usuarioId
    ){
        Unidade unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Unidade não encontrada"
                ));

        Usuario registradoPor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado."
                ));

        ContaFidelidade contaFidelidade = null;

        if (registradoPor.getRole() == Role.CLIENTE) {
            contaFidelidade = contaFidelidadeRepository
                    .findByTitular_Id(registradoPor.getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Conta de fidelidade do cliente não encontrada"
                    ));
        }

        // agrupa os produtos repetidos
        Map<Long, Integer> quantidadesPorProduto = new HashMap<>();

        for (var item : request.itens()) {
            quantidadesPorProduto.merge(
                    item.produtoId(),
                    item.quantidade(),
                    Integer::sum
            );
        }

        // consulta e valida os estoques
        Map<Long, EstoqueUnidade> estoques = new HashMap<>();

        for (var entrada : quantidadesPorProduto.entrySet()) {
            Long produtoId = entrada.getKey();
            Integer quantidadeSolicitada = entrada.getValue();

            EstoqueUnidade estoque = estoqueUnidadeRepository
                    .findByProduto_IdAndUnidade_Id(produtoId, unidade.getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Produto não encontrado no estoque desta unidade."
                    ));

            if (estoque.getQuantidade() < quantidadeSolicitada) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto ID " + produtoId + "."
                );
            }

            estoques.put(produtoId, estoque);
        }

        // criação do pedido
        Pedido pedido = new Pedido(
                unidade,
                registradoPor,
                contaFidelidade,
                request.canal()
        );

        // adiciona os itens com os preços do estoque
        for (var itemRequest : request.itens()) {
            EstoqueUnidade estoque = estoques.get(itemRequest.produtoId());

            pedido.adicionarItem(
                    estoque.getProduto(),
                    itemRequest.quantidade(),
                    estoque.getPreco()
            );
        }

        // baixa do estoque
        for (var entrada : quantidadesPorProduto.entrySet()) {
            EstoqueUnidade estoque = estoques.get(entrada.getKey());
            estoque.retirarEstoque(entrada.getValue());
        }

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return converterParaResponse(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pedido não encontrado"
                ));
        return converterParaResponse(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pedido não encontrado."
                ));

        pedido.atualizarStatus(novoStatus);

        return converterParaResponse(pedido);
    }

    @Transactional
    public PedidoResponse cancelarPedido(Long id) {
        return atualizarStatus(id, StatusPedido.CANCELADO);
    }

    private PedidoResponse converterParaResponse(Pedido pedido) {

        List<ItemPedidoResponse> itens = pedido.getItens()
                .stream()
                .map(item -> new ItemPedidoResponse(
                        item.getProduto().getId(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.calcularSubtotal()
                ))
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getUnidade().getId(),
                pedido.getRegistradoPor().getId(),
                pedido.getCanal(),
                pedido.getStatus(),
                pedido.getValorTotal(),
                pedido.getCriadoEm(),
                itens
        );
    }

}
