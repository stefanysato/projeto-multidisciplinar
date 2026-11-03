package br.com.raizesdonordeste.api.service;

import br.com.raizesdonordeste.api.dto.UsuarioRequest;
import br.com.raizesdonordeste.api.dto.UsuarioResponse;
import br.com.raizesdonordeste.api.model.Usuario;
import br.com.raizesdonordeste.api.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        // Normalização
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        // Verifica se já existe uma conta com o e-mail informado
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este e-mail já está cadastrado"
            );
        }

        // Aplica o BCrypt à senha recebida
        String senhaHash = encoder.encode(request.senha());

        // Cria o usuário usando a hash
        Usuario usuario = new Usuario(
                request.nome().trim(),
                email,
                senhaHash
        );

        Usuario usuarioSalvo = repository.save(usuario);

        return converterParaResponse(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        List<Usuario> usuarios = repository.findAll();

        return usuarios.stream()
                .map(this::converterParaResponse)
                .toList();
    }

    private Usuario buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = buscarEntidadePorId(id);

        return converterParaResponse(usuario);
    }

    @Transactional
    public void ativarUsuario(Long id) {
        Usuario usuario = buscarEntidadePorId(id);

        usuario.ativar();
    }

    @Transactional
    public void desativarUsuario(Long id) {
        Usuario usuario = buscarEntidadePorId(id);

        usuario.desativar();
    }

    private UsuarioResponse converterParaResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.isAtivo(),
                usuario.getCriadoEm()
        );
    }
}