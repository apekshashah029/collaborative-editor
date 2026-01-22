package com.example.websocket.service;

import com.example.websocket.dto.UserRequestDTO;
import com.example.websocket.dto.UserResponseDTO;
import com.example.websocket.entity.Role;
import com.example.websocket.entity.User;
import com.example.websocket.exception.UserRegistrationException;
import com.example.websocket.exception.UsernameAlreadyExistsException;
import com.example.websocket.mapper.UserMapper;
import com.example.websocket.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public CustomUserDetailService(UserRepository userRepo, PasswordEncoder passwordEncoder, UserMapper userMapper){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

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
    public UserResponseDTO doSignUp(UserRequestDTO userDTO){
        if (userRepo.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        try{
            User user = userMapper.toEntity(userDTO);
            user.setRole(Role.USER);
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            User savedUser = userRepo.save(user);

            return userMapper.toResponse(savedUser);
        }catch (Exception e){
            throw new UserRegistrationException("Failed to register user");
        }

    }
}
