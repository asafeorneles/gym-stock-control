package com.asafeorneles.gymstock.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_refresh_tokens")

@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "refresh_token_id")
    private UUID refreshTokenId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "expires_date", nullable = false)
    private Instant expiresDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public RefreshToken(
            String token,
            boolean revoked,
            Instant expiresDate,
            User user) {

        this.token = token;
        this.revoked = revoked;
        this.expiresDate = expiresDate;
        this.user = user;
    }
}
