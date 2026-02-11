package com.example.websocket.service;

import com.example.websocket.dto.LoginRequestDTO;
import com.example.websocket.entity.type.Role;
import com.example.websocket.entity.User;
import com.example.websocket.exception.UserRegistrationException;
import com.example.websocket.exception.UsernameAlreadyExistsException;
import com.example.websocket.mapper.UserMapper;
import com.example.websocket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        )
                )
        );
    }

    @Transactional
    public void doSignUp(LoginRequestDTO loginDTO, String refreshToken) {

        if (userRepo.findByUsername(loginDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        try {
            User user = userMapper.toEntity(loginDTO);
            user.setRole(Role.USER);
            user.setRefresh_token(refreshToken);

            if (loginDTO.getPassword() != null && !loginDTO.getPassword().isBlank()) {
                // local user
                user.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
            } else {
                // OAuth user
                user.setPassword(null);
            }

            User savedUser = userRepo.save(user);
            userMapper.toResponse(savedUser);

        } catch (Exception e) {
            throw new UserRegistrationException("Failed to register user");
        }
    }

}
