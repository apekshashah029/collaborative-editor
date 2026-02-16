package com.example.websocket.mapper;

import com.example.websocket.dto.LoginRequestDTO;
import com.example.websocket.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(LoginRequestDTO dto);
}
