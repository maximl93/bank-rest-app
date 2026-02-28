package com.example.bankcards.controller;


import com.example.bankcards.dto.card.CardBalanceResponseDTO;
import com.example.bankcards.dto.card.CardDTO;
import com.example.bankcards.dto.transaction.TransactionResultDTO;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.security.JwtAuthFilter;
import com.example.bankcards.security.JwtProvider;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;

@WebMvcTest(CardRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CardRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private TransactionService transactionService;

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
    void getUserCardsTest() throws Exception {
        Page<CardDTO> page = new PageImpl<>(List.of(new CardDTO()));

        when(cardService.findUserCards(anyInt(), anyInt(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/cards")
                        .with(token)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void findCardBalanceTest() throws Exception {
        BigDecimal balance = new BigDecimal(1000);
        CardBalanceResponseDTO dto = new CardBalanceResponseDTO(1L, "**** **** **** 1234", balance);

        when(cardService.findCardBalance(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/cards/1/balance")
                        .with(token))
                .andExpect(status().isOk())
                .andDo(print());

        verify(cardService).findCardBalance(1L);
    }

    @Test
    void requestBlockCardTest() throws Exception {

        mockMvc.perform(patch("/api/cards/1/requestBlock")
                        .with(token))
                .andExpect(status().isOk())
                .andDo(print());

        verify(cardService).requestBlock(1L);
    }

    @Test
    void transactionTest() throws Exception {
        BigDecimal fromBalance = new BigDecimal(1000);
        BigDecimal toBalance = new BigDecimal(2000);
        TransactionResultDTO result = new TransactionResultDTO(fromBalance, toBalance, TransactionStatus.SUCCESS);

        when(transactionService.makeTransaction(any()))
                .thenReturn(result);

        mockMvc.perform(post("/api/cards/transfer")
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(result)))
                .andExpect(status().isOk());
    }
}
