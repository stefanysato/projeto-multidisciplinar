package br.com.raizesdonordeste.api.service;

import br.com.raizesdonordeste.api.dto.ProdutoRequest;
import br.com.raizesdonordeste.api.dto.ProdutoResponse;
import br.com.raizesdonordeste.api.model.Produto;
import br.com.raizesdonordeste.api.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) { this.repository = repository; }


    @Transactional
    public ProdutoResponse cadastrar(ProdutoRequest request) {

        Produto produto = new Produto(
          request.nome().trim(),
          normalizarDescricao(request.descricao()),
          request.categoria()
        );

        Produto produtoSalvo = repository.save(produto);

        return converterParaResponse(produtoSalvo);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarProdutos() {
        return repository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        Produto produto =  buscarEntidadePorId(id);
        return converterParaResponse(produto);
    }

    @Transactional
    public void ativar(Long id) {
        Produto produto = buscarEntidadePorId(id);
        produto.ativar();
    }

    @Transactional
    public void desativar(Long id) {
        Produto produto = buscarEntidadePorId(id);
        produto.desativar();
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidadePorId(id);

        produto.atualizarDados(
                request.nome().trim(),
                normalizarDescricao(request.descricao()),
                request.categoria()
        );
        return converterParaResponse(produto);
    }


    private Produto buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado"
                ));
    }


    public ProdutoResponse converterParaResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getCategoria(),
                produto.isAtivo(),
                produto.getCriadoEm()
        );
    }

    // descrição opcional
    private String normalizarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return null;
        }

        return descricao.trim();
   }

}
