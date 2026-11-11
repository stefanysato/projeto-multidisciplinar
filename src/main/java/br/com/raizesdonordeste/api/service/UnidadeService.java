package br.com.raizesdonordeste.api.service;

import br.com.raizesdonordeste.api.dto.UnidadeRequest;
import br.com.raizesdonordeste.api.dto.UnidadeResponse;
import br.com.raizesdonordeste.api.model.Unidade;
import br.com.raizesdonordeste.api.repository.UnidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class UnidadeService {

    private final UnidadeRepository repository;

    public UnidadeService(UnidadeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UnidadeResponse cadastrar(UnidadeRequest request) {

        Unidade unidade = new Unidade(
                request.nome().trim(),
                request.telefone(),
                request.logradouro().trim(),
                request.numero().trim(),
                request.bairro().trim(),
                request.cidade().trim(),
                request.estado().trim().toUpperCase(Locale.ROOT),
                request.cep().trim()
        );

        Unidade unidadeSalva = repository.save(unidade);

        return converterParaResponse(unidadeSalva);
    }

    @Transactional(readOnly = true)
    public List<UnidadeResponse> listarTodas() {
        return repository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnidadeResponse buscarPorId(Long id) {
        Unidade unidade = buscarEntidadePorId(id);
        return converterParaResponse(unidade);
    }

    @Transactional
    public void ativar(Long id) {
        Unidade unidade = buscarEntidadePorId(id);
        unidade.ativar();
    }

    @Transactional
    public void desativar(Long id) {
        Unidade unidade = buscarEntidadePorId(id);
        unidade.desativar();
    }

    @Transactional
    public UnidadeResponse atualizar(Long id, UnidadeRequest request) {
        Unidade unidade = buscarEntidadePorId(id);

        unidade.atualizarDados(
                request.nome().trim(),
                request.telefone(),
                request.logradouro().trim(),
                request.numero().trim(),
                request.bairro().trim(),
                request.cidade().trim(),
                request.estado().trim().toUpperCase(Locale.ROOT),
                request.cep().trim()
        );
        return converterParaResponse(unidade);
    }

    private Unidade buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unidade não encontrada"
                ));
    }

    private UnidadeResponse converterParaResponse(Unidade unidade) {
        return new UnidadeResponse(
                unidade.getId(),
                unidade.getNome(),
                unidade.getTelefone(),
                unidade.getLogradouro(),
                unidade.getNumero(),
                unidade.getBairro(),
                unidade.getCidade(),
                unidade.getEstado(),
                unidade.getCep(),
                unidade.isAtiva(),
                unidade.getCriadaEm()
        );
    }
}
