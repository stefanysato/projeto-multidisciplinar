package br.com.raizesdonordeste.api.service;

import br.com.raizesdonordeste.api.dto.LoginRequest;
import br.com.raizesdonordeste.api.security.JwtService;
import br.com.raizesdonordeste.api.security.UsuarioDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String autenticar(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().trim(),
                        request.senha()
                )
        );

        UsuarioDetails usuario = (UsuarioDetails) authentication.getPrincipal();

        return jwtService.gerarToken(usuario);

    }

}
