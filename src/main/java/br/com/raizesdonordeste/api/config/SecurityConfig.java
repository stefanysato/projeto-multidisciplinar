package br.com.raizesdonordeste.api.config;

import br.com.raizesdonordeste.api.security.JwtAuthenticationFilter;
import br.com.raizesdonordeste.api.security.JwtService;
import br.com.raizesdonordeste.api.security.UsuarioDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            UsuarioDetailsService usuarioDetailsService
    ) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(
                        jwtService,
                        usuarioDetailsService
                );

        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                // STATELESS = o Spring Security não deve criar
                                // nem utilizar sessões HTTP para guardar
                                // o contexto de autenticação
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"erro\": \"Autenticação necessária\"}"
                            );
                        })

                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"erro\": \"Você não tem permissão para acessar este recurso\"}"
                            );
                        })
                )

                .authorizeHttpRequests(auth -> auth
                        // cadastro de clientes
                        .requestMatchers(
                                HttpMethod.POST,
                                "/usuarios"
                        ).permitAll()

                        // login
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/login"
                        ).permitAll()

                        // consulta do próprio perfil pelo usuário autenticado
                        .requestMatchers(
                                HttpMethod.GET,
                                "/usuarios/perfil"
                        ).authenticated()

                        // listar usuários
                        .requestMatchers(
                                HttpMethod.GET,
                                "/usuarios", "/usuarios/**"
                        ).hasRole("ADMINISTRADOR")

                        // ativar/desativar usuario
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/usuarios/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/unidades"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/unidades/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/unidades", "/unidades/**")
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/unidades/**"
                        ).hasRole("ADMINISTRADOR")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

}
