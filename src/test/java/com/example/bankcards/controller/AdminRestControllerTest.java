package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardDTO;
import com.example.bankcards.dto.user.UserDTO;
import com.example.bankcards.security.JwtAuthFilter;
import com.example.bankcards.security.JwtProvider;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        token = jwt().jwt(builder -> builder.claim("sub", "userTest@mail.com").claim("roles", "ADMIN"));
    }


    @Test
    void createUserTest() throws Exception {
        UserDTO response = new UserDTO();
        response.setId(1L);
        response.setEmail("userTest@mail.com");
        response.setRoleId(1L);

        when(userService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/users")
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated());

        verify(userService).create(any());
    }

    @Test
    void findUserByIdTest() throws Exception {

        when(userService.findById(1L)).thenReturn(new UserDTO());

        mockMvc.perform(get("/api/admin/users/1")
                        .with(token))
                .andExpect(status().isOk());

        verify(userService).findById(1L);
    }

    @Test
    void findAllUsersTest() throws Exception {

        when(userService.findAll()).thenReturn(List.of(new UserDTO()));

        mockMvc.perform(get("/api/admin/users")
                        .with(token))
                .andExpect(status().isOk());

        verify(userService).findAll();
    }

    @Test
    void updateUserTest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("userTest@mail.com");
        userDTO.setRoleId(1L);

        when(userService.update(any(), eq(1L))).thenReturn(userDTO);

        mockMvc.perform(put("/api/admin/users/1")
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());

        verify(userService).update(any(), eq(1L));
    }

    @Test
    void deleteUserTest() throws Exception {

        mockMvc.perform(delete("/api/admin/users/1")
                        .with(token))
                .andExpect(status().isNoContent());

        verify(userService).deleteById(1L);
    }

    @Test
    void addUserCardTest() throws Exception {

        when(userService.addUserCard(eq(1L), any())).thenReturn(new CardDTO());

        String json = "{\"cardId\": 1}";

        mockMvc.perform(post("/api/admin/users/1/cards")
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(userService).addUserCard(eq(1L), any());
    }

    @Test
    void deleteUserCardTest() throws Exception {

        mockMvc.perform(delete("/api/admin/users/1/cards/2")
                        .with(token))
                .andExpect(status().isNoContent());

        verify(userService).deleteUserCard(1L, 2L);
    }

    @Test
    void createCardTest() throws Exception {

        when(cardService.create(any())).thenReturn(new CardDTO());

        String json = """
            {
                "cardNumber": "1234123412341234"
            }
            """;

        mockMvc.perform(post("/api/admin/cards")
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(cardService).create(any());
    }

    @Test
    void blockCardTest() throws Exception {

        when(cardService.blockCard(1L)).thenReturn(new CardDTO());

        mockMvc.perform(put("/api/admin/cards/1/block")
                        .with(token))
                .andExpect(status().isOk());

        verify(cardService).blockCard(1L);
    }

    @Test
    void deleteCardTest() throws Exception {

        mockMvc.perform(delete("/api/admin/cards/1")
                        .with(token))
                .andExpect(status().isNoContent());

        verify(cardService).deleteById(1L);
    }
}
