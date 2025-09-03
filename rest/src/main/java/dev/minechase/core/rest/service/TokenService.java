package dev.minechase.core.rest.service;

import dev.minechase.core.rest.model.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder encoder;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expires-in}")
    private long expiresInSeconds;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(Account account) {
        Instant now = Instant.now();

//        String scope = account.getRoles().stream()
//                .map(Role::getName)
//                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresInSeconds))
                .subject(account.getEmail())
//                .claim("scope", scope)
                .build();

        var headers = JwsHeader.with(MacAlgorithm.HS256).build();

        return encoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }

}
