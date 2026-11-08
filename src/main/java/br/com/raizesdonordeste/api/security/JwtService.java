package br.com.raizesdonordeste.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        this.expiration = expiration;
    }

    public String gerarToken(UsuarioDetails usuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiration);

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("uid", usuario.getId())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(secretKey)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public boolean validarToken(String token, UserDetails userDetails) {

        try {
            String email = extrairEmail(token);
            return email.equals(userDetails.getUsername())
                    && !tokenExpirado(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

    }

    private boolean tokenExpirado(String token) {
        Date expiracao = extrairClaim(token, Claims::getExpiration);

        return expiracao.before(new Date());
    }

    private <T> T extrairClaim(
            String token,
            Function<Claims, T> resolver
    ) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

}
