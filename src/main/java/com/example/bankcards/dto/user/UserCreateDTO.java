package com.example.bankcards.dto.user;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserCreateDTO {
    private String email;
    private String password;
    private Long roleId;
}
