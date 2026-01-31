package com.asafeorneles.gymstock.entities;

import com.asafeorneles.gymstock.enums.ActivityStatus;
import com.asafeorneles.gymstock.exceptions.ActivityStatusException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_users")

@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tb_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Column(name = "activity_status")
    @Enumerated(EnumType.STRING)
    private ActivityStatus activityStatus;

    @Column(name = "registered_date")
    private LocalDateTime registeredDate;

    @PrePersist
    public void prePersist() {
        this.registeredDate = LocalDateTime.now();
    }

    public void inactivity(){
        if (this.activityStatus == ActivityStatus.INACTIVITY){
            throw new ActivityStatusException("User is already inactive!");
        }

        this.activityStatus = ActivityStatus.INACTIVITY;
    }

    public void activity(){
        if (this.activityStatus == ActivityStatus.ACTIVE){
            throw new ActivityStatusException("User is already active!");
        }
        this.activityStatus = ActivityStatus.ACTIVE;
    }

    public Boolean isActivity(){
        return this.activityStatus == ActivityStatus.ACTIVE;
    }

    @Builder
    public User(UUID userId, String username, String password, Set<Role> roles) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
}
