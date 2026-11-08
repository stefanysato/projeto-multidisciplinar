package br.com.raizesdonordeste.api.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioDetailsService usuarioDetailsService
    ) {
       this.jwtService = jwtService;
       this.usuarioDetailsService = usuarioDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // request = requisição http recebida
        // getHeader = acessa os cabeçalhos enviados pelo usuario
        String header = request.getHeader("Authorization");

        // se não houver token, continua a cadeia de filtros
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // remove o prefixo 'Bearer ' do token
        String token = header.substring(7);

        try {
            // identifica o usuário
            String email = jwtService.extrairEmail(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // localiza o usuário no banco de dados
                UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);

                if (jwtService.validarToken(token, userDetails)
                    && userDetails.isEnabled()) {

                    // cria um objeto que representa uma autenticação já validada
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException | IllegalArgumentException |UsernameNotFoundException e) {
            // limpa o contexto se o token for inválido, expirado ou
            // pertencer a um usuário não existente
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);

    }

}
