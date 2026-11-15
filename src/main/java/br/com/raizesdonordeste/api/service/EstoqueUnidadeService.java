package br.com.raizesdonordeste.api.service;

import br.com.raizesdonordeste.api.dto.AtualizarPrecoRequest;
import br.com.raizesdonordeste.api.dto.EstoqueUnidadeRequest;
import br.com.raizesdonordeste.api.dto.EstoqueUnidadeResponse;
import br.com.raizesdonordeste.api.dto.MovimentarEstoqueRequest;
import br.com.raizesdonordeste.api.model.EstoqueUnidade;
import br.com.raizesdonordeste.api.model.Produto;
import br.com.raizesdonordeste.api.model.Unidade;
import br.com.raizesdonordeste.api.repository.EstoqueUnidadeRepository;
import br.com.raizesdonordeste.api.repository.ProdutoRepository;
import br.com.raizesdonordeste.api.repository.UnidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EstoqueUnidadeService {

    private final EstoqueUnidadeRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;

    public EstoqueUnidadeService(
        EstoqueUnidadeRepository estoqueRepository,
        ProdutoRepository produtoRepository,
        UnidadeRepository unidadeRepository
    ) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.unidadeRepository = unidadeRepository;
    }

    @Transactional
    public EstoqueUnidadeResponse cadastrar(EstoqueUnidadeRequest request) {
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado."
                ));

        Unidade unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unidade não encontrada."
                ));

        boolean jaExiste = estoqueRepository.existsByProduto_IdAndUnidade_Id(
                produto.getId(), unidade.getId()
        );

        if (jaExiste) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este produto já está associado a esta unidade."
            );
        }

        EstoqueUnidade estoque = new EstoqueUnidade(
                produto,
                unidade,
                request.quantidade(),
                request.preco()
        );

        EstoqueUnidade salvo = estoqueRepository.save(estoque);

        return converterParaResponse(salvo);
    }

    @Transactional
    public List<EstoqueUnidadeResponse> listarPorUnidade(
            Long unidadeId
    ) {
        if (!unidadeRepository.existsById(unidadeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Unidade não encontrada"
            );
        }
        return estoqueRepository.findAllByUnidade_Id(unidadeId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstoqueUnidadeResponse buscarPorId(Long id) {
        EstoqueUnidade estoque = buscarEntidadePorId(id);
        return converterParaResponse(estoque);
    }

    @Transactional
    public EstoqueUnidadeResponse atualizarPreco(
            Long id,
            AtualizarPrecoRequest request
    ) {
        EstoqueUnidade estoque = buscarEntidadePorId(id);
        estoque.atualizarPreco(request.preco());
        return converterParaResponse(estoque);
    }

    @Transactional
    public EstoqueUnidadeResponse registrarEntrada(
            Long id,
            MovimentarEstoqueRequest request
    ) {
        EstoqueUnidade estoque = buscarEntidadePorId(id);
        estoque.adicionarEstoque(request.quantidade());
        return converterParaResponse(estoque);
    }

    @Transactional
    public EstoqueUnidadeResponse registrarSaida(
            Long id,
            MovimentarEstoqueRequest request
    ) {
        EstoqueUnidade estoque = buscarEntidadePorId(id);
        try {
            estoque.retirarEstoque(request.quantidade());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
        return converterParaResponse(estoque);
    }

    private EstoqueUnidade buscarEntidadePorId(Long id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registro de estoque não encontrado"
                ));
    }

    private EstoqueUnidadeResponse converterParaResponse(EstoqueUnidade estoque) {
       return new EstoqueUnidadeResponse(
               estoque.getId(),
               estoque.getProduto().getId(),
               estoque.getProduto().getNome(),
               estoque.getProduto().getCategoria(),
               estoque.getUnidade().getId(),
               estoque.getUnidade().getNome(),
               estoque.getQuantidade(),
               estoque.getPreco()
       );
    }
}
