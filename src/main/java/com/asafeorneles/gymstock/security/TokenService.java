package com.asafeorneles.gymstock.security;

import com.asafeorneles.gymstock.entities.User;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.repositories.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TokenService {

    @Autowired
    JwtEncoder jwtEncoder;

    @Autowired
    UserRepository userRepository;

    @Getter
    @Value("${jwt.expiration}")
    private Long accessTokenExpiration;

    @Getter
    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    private final String refreshType = "refresh";
    private final String accessType = "access";

    private String generateToken(Authentication authentication, Long expiration, String tokenType) {

        String scopes = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found creating JWT accessToken"));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("gym-stock-api")
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .claim("scopes", scopes)
                .claim("username", username)
                .claim("type", tokenType)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String getAccessToken(Authentication authentication){
        return generateToken(authentication, accessTokenExpiration, accessType);
    }

    public String getRefreshToken(Authentication authentication){
        return generateToken(authentication, refreshTokenExpiration, refreshType);
    }

}
