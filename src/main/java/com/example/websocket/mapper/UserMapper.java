package com.example.websocket.mapper;

import com.example.websocket.dto.LoginRequestDTO;
import com.example.websocket.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = IMapper.class)
public interface UserMapper {

    User toEntity(LoginRequestDTO dto);
}
