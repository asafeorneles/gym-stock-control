package com.asafeorneles.gym_stock_control.security;

import com.asafeorneles.gym_stock_control.entities.User;
import com.asafeorneles.gym_stock_control.enums.Permission;
import com.asafeorneles.gym_stock_control.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by this username: " + username));

        Set<SimpleGrantedAuthority> authorities = permissionAdd(user);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(!user.isActivity())
                .build();
    }

    private Set<SimpleGrantedAuthority> permissionAdd(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));

            switch (role.getName()) {
                case "ROLE_ADMIN":
                    Arrays.stream(Permission.values())
                            .forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getPermission())));
                    break;

                case "ROLE_BASIC":

                    authorities.add(new SimpleGrantedAuthority(Permission.PRODUCT_READ.getPermission()));
                    authorities.add(new SimpleGrantedAuthority(Permission.CATEGORY_READ.getPermission()));
                    authorities.add(new SimpleGrantedAuthority(Permission.PRODUCT_INVENTORY_READ.getPermission()));
                    authorities.add(new SimpleGrantedAuthority(Permission.COUPON_READ.getPermission()));
                    authorities.add(new SimpleGrantedAuthority(Permission.SALE_CREATE.getPermission()));
                    authorities.add(new SimpleGrantedAuthority(Permission.SALE_READ.getPermission()));
                    break;
            }
        });
        return authorities;
    }
}
