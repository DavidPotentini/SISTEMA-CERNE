package com.github.davidpotentini.configuration.security;
import com.github.davidpotentini.comum.tenant.UsuarioAutenticado;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMin;

    public JwtService(@Value("${app.jwt.secret}") String secretBase64,
                      @Value("${app.jwt.expiracao-min}") long expiracaoMin) {
        this.chave = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretBase64));
        this.expiracaoMin = expiracaoMin;
    }

    /**
     * Gera o token a partir da identidade resolvida no login. Claims do modelo novo:
     * a conta global ({@code ctaCod}, subject = e-mail) vive em {@code public.CONTAS};
     * o tenant ({@code nomeSchema}) e o papel local ({@code papCod}/{@code papelNome})
     * vêm da incubadora escolhida.
     */
    public String gerar(UsuarioAutenticado usuario) {
        return Jwts.builder()
                .subject(usuario.email())
                .claim("ctaCod", usuario.ctaCod())
                .claim("nomeSchema", usuario.nomeSchema())
                .claim("papCod", usuario.papCod())
                .claim("papelNome", usuario.papelNome())
                .claim("adminPlataforma", usuario.adminPlataforma())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(expiracaoMin, ChronoUnit.MINUTES)))
                .signWith(chave)
                .compact();
    }

    public Claims validar(String token){
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
