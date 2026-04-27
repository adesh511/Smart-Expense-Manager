package com.smartexpense.security;

import com.smartexpense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long id = Long.parseLong(username);
        return userRepository.findById(id)
                .map(AppUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
