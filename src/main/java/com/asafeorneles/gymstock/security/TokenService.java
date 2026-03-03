package com.asafeorneles.gymstock.security;

import com.asafeorneles.gymstock.entities.User;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.repositories.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    final JwtEncoder jwtEncoder;
    final UserRepository userRepository;

    @Getter
    @Value("${jwt.expiration}")
    private Long accessTokenExpiration;

    @Getter
    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    private final String refreshType = "refresh";
    private final String accessType = "access";

    private String generateToken(Authentication authentication, Long expiration, String tokenType, String jti) {

        String scopes = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found creating JWT accessToken"));

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("gym-stock-api")
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .claim("scopes", scopes)
                .claim("username", username)
                .claim("type", tokenType);


        if (jti != null){
            claimsBuilder.id(jti);
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
    }

    public String getAccessToken(Authentication authentication){
        return generateToken(authentication, accessTokenExpiration, accessType, null);
    }

    public String getRefreshToken(Authentication authentication, String jti){
        return generateToken(authentication, refreshTokenExpiration, refreshType, jti);
    }

}
