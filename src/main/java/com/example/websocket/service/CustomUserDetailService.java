package com.example.websocket.service;

import com.example.websocket.dto.UserRequestDTO;
import com.example.websocket.dto.UserResponseDTO;
import com.example.websocket.entity.User;
import com.example.websocket.exception.UserRegistrationException;
import com.example.websocket.exception.UsernameAlreadyExistsException;
import com.example.websocket.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailService(UserRepository userRepo, PasswordEncoder passwordEncoder){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Transactional
    public UserResponseDTO doSignUp(UserRequestDTO userDTO){
        if (userRepo.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        try{
            User user = new User();
            user.setUid(UUID.randomUUID());
            user.setUsername(userDTO.getUsername());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            User savedUser = userRepo.save(user);
            UserResponseDTO response = new UserResponseDTO(savedUser.getUsername());

            return response;
        }catch (Exception e){
            throw new UserRegistrationException("Failed to register user");
        }

    }
}
