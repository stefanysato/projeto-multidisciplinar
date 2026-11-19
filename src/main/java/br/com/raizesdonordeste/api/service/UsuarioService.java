package br.com.raizesdonordeste.api.service;

import br.com.raizesdonordeste.api.dto.FuncionarioRequest;
import br.com.raizesdonordeste.api.dto.FuncionarioResponse;
import br.com.raizesdonordeste.api.dto.UsuarioRequest;
import br.com.raizesdonordeste.api.dto.UsuarioResponse;
import br.com.raizesdonordeste.api.model.ContaFidelidade;
import br.com.raizesdonordeste.api.model.Role;
import br.com.raizesdonordeste.api.model.Unidade;
import br.com.raizesdonordeste.api.model.Usuario;
import br.com.raizesdonordeste.api.repository.ContaFidelidadeRepository;
import br.com.raizesdonordeste.api.repository.UnidadeRepository;
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

    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ContaFidelidadeRepository contaFidelidadeRepository;
    private final PasswordEncoder encoder;

    public UsuarioService(
            UsuarioRepository repository,
            UnidadeRepository unidadeRepository,
            ContaFidelidadeRepository contaFidelidadeRepository,
            PasswordEncoder encoder
    ) {
        this.usuarioRepository = repository;
        this.unidadeRepository = unidadeRepository;
        this.contaFidelidadeRepository = contaFidelidadeRepository;
        this.encoder = encoder;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        // Normalização
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        // Verifica se já existe uma conta com o e-mail informado
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este e-mail já está cadastrado."
            );
        }

        // Aplica o BCrypt à senha recebida
        String senhaHash = encoder.encode(request.senha());

        // Cria o usuário usando a hash
        Usuario usuario = new Usuario(
                request.nome().trim(),
                email,
                senhaHash,
                Role.CLIENTE
        );

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        ContaFidelidade conta = new ContaFidelidade(usuarioSalvo);
        contaFidelidadeRepository.save(conta);

        return converterParaResponse(usuarioSalvo);
    }

    @Transactional
    public FuncionarioResponse cadastrarFuncionario(FuncionarioRequest request) {
        if (request.role() != Role.ATENDENTE
                && request.role() != Role.COZINHA
                && request.role() != Role.GERENTE) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Perfil inválido para cadastro de funcionário."
            );
        }
        String email = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este e-mail já está cadastrado."
            );
        }

        Unidade unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unidade não encontrada."
                ));

        if (!unidade.isAtiva()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível associar funcionários a uma unidade inativa."
            );
        }

        String senhaHash = encoder.encode(request.senha());

        Usuario usuario = new Usuario(
                request.nome().trim(),
                email,
                senhaHash,
                request.role(),
                unidade
        );

        Usuario salvo = usuarioRepository.save(usuario);

        return converterParaFuncionarioResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(this::converterParaResponse)
                .toList();
    }

    private Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
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

    private FuncionarioResponse converterParaFuncionarioResponse(
            Usuario usuario
    ) {
        return new FuncionarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getUnidade().getId(),
                usuario.getUnidade().getNome(),
                usuario.isAtivo(),
                usuario.getCriadoEm()
        );
    }
}