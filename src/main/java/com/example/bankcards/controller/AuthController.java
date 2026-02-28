package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthRequestDTO;
import com.example.bankcards.security.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorization")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Operation(
            summary = "Авторизация",
            description = "Для авторизации зарегистрированных пользователей"
    )
    @PostMapping("/login")
    public String login(@RequestBody AuthRequestDTO requestDTO) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                requestDTO.getEmail(),
                                requestDTO.getPassword()
                        )
                );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return jwtProvider.generateToken(userDetails);
    }
}
