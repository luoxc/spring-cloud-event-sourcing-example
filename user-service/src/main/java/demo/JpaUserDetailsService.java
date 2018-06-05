package demo;

import demo.user.User;
import demo.user.UserRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        return DefaultUserDetails.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Stream.of(user.getRoles())
                        .filter(Objects::nonNull)
                        .flatMap(e -> Stream.of(e.split(",")))
                        .filter(Objects::nonNull)
                        .map(String::toUpperCase)
                        .map(e -> new SimpleGrantedAuthority(e))
                        .collect(Collectors.toList()))
                .build();
    }


    @Builder
    public static class DefaultUserDetails implements UserDetails {

        private String username;
        private String password;
        private Collection<GrantedAuthority> authorities;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

}
