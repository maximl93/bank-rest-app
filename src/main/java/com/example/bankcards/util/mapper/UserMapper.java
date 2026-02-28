package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.user.UserCreateDTO;
import com.example.bankcards.dto.user.UserDTO;
import com.example.bankcards.dto.user.UserUpdateDTO;
import com.example.bankcards.entity.User;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        uses = {ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING
)
@RequiredArgsConstructor
public abstract class UserMapper {

    @Mapping(target = "role", source = "roleId")
    public abstract User map(UserCreateDTO createDTO);
    @Mapping(target = "roleId", source = "role.id")
    public abstract UserDTO map(User user);
    public abstract List<UserDTO> map(List<User> users);
    public abstract void update(UserUpdateDTO updateDTO, @MappingTarget User user);
}
