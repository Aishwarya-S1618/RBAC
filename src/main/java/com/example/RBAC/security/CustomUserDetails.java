package com.example.RBAC.security;

import com.example.RBAC.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom implementation of UserDetails to provide user-specific data to Spring Security.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * Constructor to initialize CustomUserDetails with a User entity.
     * @param user The User entity containing user details.
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Retrieves the authorities (roles) granted to the user.
     * @return A collection of GrantedAuthority representing the user's roles.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toSet());

        System.out.println("🔹 Assigned Roles in CustomUserDetails: " + authorities);
        return authorities;
    }

    /**
     * Retrieves the password of the user.
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Retrieves the username of the user.
     * @return The user's username.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the user's account is expired.
     * @return true if the account is not expired, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is locked.
     * @return true if the account is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials are expired.
     * @return true if the credentials are not expired, false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     * @return true if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
