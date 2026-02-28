package com.example.bankcards.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String email;
    private Long roleId;
}
