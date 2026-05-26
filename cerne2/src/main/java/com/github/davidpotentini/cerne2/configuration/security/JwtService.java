package com.github.davidpotentini.cerne2.configuration.security;

import com.github.davidpotentini.cerne2.enums.ERole;
import com.github.davidpotentini.cerne2.models.cadastro.TenantsModel;
import com.github.davidpotentini.cerne2.models.cadastro.UsersModel;
import com.github.davidpotentini.cerne2.models.pessoas.PessoasModel;
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

    public String gerar(UsersModel usuario, TenantsModel tenant, PessoasModel pessoa, ERole role){

        Long incCod = pessoa.getIncubadasModel() != null
                ? pessoa.getIncubadasModel().getIncCod()
                : null;

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("usnCod", usuario.getUsnCod())
                .claim("pessoaCod", pessoa.getPessoaCod())
                .claim("tntCod", tenant.getTntCod())
                .claim("nomeSchema", tenant.getNomeSchema())
                .claim("role", role.name())
                .claim("incCod", incCod)
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
