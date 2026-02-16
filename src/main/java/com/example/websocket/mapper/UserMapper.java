package com.example.websocket.mapper;

import com.example.websocket.dto.LoginRequestDTO;
import com.example.websocket.dto.UserResponseDTO;
import com.example.websocket.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(LoginRequestDTO dto);

    UserResponseDTO toResponse(User user);
}
