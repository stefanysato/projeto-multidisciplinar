package br.com.raizesdonordeste.api.security;

import br.com.raizesdonordeste.api.model.Usuario;
import br.com.raizesdonordeste.api.repository.UsuarioRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) {
        Usuario usuario = repository
                .findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado"
                ));

        return new UsuarioDetails(usuario);
    } 

}
