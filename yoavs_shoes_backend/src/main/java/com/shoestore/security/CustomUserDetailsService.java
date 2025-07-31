package com.shoestore.security;

import com.shoestore.entity.user.User;
import com.shoestore.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Custom UserDetailsService implementation for Spring Security
 *
 * This service loads user-specific data during authentication.
 * It retrieves user information from the database and converts it
 * to Spring Security's UserDetails format.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by username (email in our case)
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));

        return UserPrincipal.create(user);
    }

    /**
     * Load user by ID (useful for JWT token validation)
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with id: " + id));

        return UserPrincipal.create(user);
    }

    /**
     * Custom UserPrincipal class that implements UserDetails
     */
    public static class UserPrincipal implements UserDetails {
        private Long id;
        private String email;
        private String password;
        private Collection<? extends GrantedAuthority> authorities;
        private boolean accountNonLocked;

        public UserPrincipal(Long id, String email, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             boolean accountNonLocked) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.authorities = authorities;
            this.accountNonLocked = accountNonLocked;
        }

        public static UserPrincipal create(User user) {
            List<GrantedAuthority> authorities = new ArrayList<>();

            // Add role-based authority
            if (user.getIsAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            return new UserPrincipal(
                    user.getId(),
                    user.getEmail(),
                    user.getPasswordHash(),
                    authorities,
                    !user.isAccountLocked()
            );
        }

        public Long getId() {
            return id;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return accountNonLocked;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserPrincipal that = (UserPrincipal) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}