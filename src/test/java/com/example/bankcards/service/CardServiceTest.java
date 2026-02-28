package com.example.bankcards.service;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceAlreadyExistsException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.JwtAuthFilter;
import com.example.bankcards.security.JwtProvider;
import com.example.bankcards.util.CardNumberEncryptor;
import com.example.bankcards.util.CardOwnerCheck;
import com.example.bankcards.util.CardSpecification;
import com.example.bankcards.util.CurrentUserCheck;
import com.example.bankcards.util.mapper.CardMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CurrentUserCheck currentUserCheck;

    @Mock
    private CardOwnerCheck cardOwnerCheck;

    @Mock
    private CardSpecification cardSpecification;

    @Mock
    private CardNumberEncryptor cardNumberEncryptor;

    @InjectMocks
    private CardService cardService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void createCardSuccessfullyTest() {

        CardCreateDTO createDTO = new CardCreateDTO();
        createDTO.setNumber("1234123412341234");

        Card card = new Card();
        CardDTO response = new CardDTO();

        when(cardNumberEncryptor.encrypt(createDTO.getNumber())).thenReturn("encrypted");
        when(cardRepository.findByNumber("encrypted")).thenReturn(Optional.empty());
        when(cardMapper.map(createDTO)).thenReturn(card);
        when(cardMapper.map(card)).thenReturn(response);

        CardDTO result = cardService.create(createDTO);

        assertNotNull(result);
        verify(cardRepository).save(card);
    }

    @Test
    void createCardWithNumberAlreadyExistTest() {

        CardCreateDTO createDTO = new CardCreateDTO();
        createDTO.setNumber("1234123412341234");

        when(cardNumberEncryptor.encrypt(createDTO.getNumber())).thenReturn("encrypted");
        when(cardRepository.findByNumber("encrypted"))
                .thenReturn(Optional.of(new Card()));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> cardService.create(createDTO));

        verify(cardRepository, never()).save(any());
    }


    @Test
    void findCardByIdTest() {

        Card card = new Card();
        CardDTO dto = new CardDTO();

        when(cardOwnerCheck.getUserOwnedCard(1L)).thenReturn(card);
        when(cardMapper.map(card)).thenReturn(dto);

        CardDTO result = cardService.findById(1L);

        assertEquals(dto, result);
    }

    @Test
    void updateCardTest() {

        CardUpdateDTO updateDTO = new CardUpdateDTO();
        Card card = new Card();
        CardDTO dto = new CardDTO();

        when(cardOwnerCheck.getCardOrThrowException(1L)).thenReturn(card);
        when(cardMapper.map(card)).thenReturn(dto);

        CardDTO result = cardService.update(updateDTO, 1L);

        verify(cardMapper).update(updateDTO, card);
        verify(cardRepository).save(card);
        assertEquals(dto, result);
    }

    @Test
    void setStatusActiveTest() {

        Card card = new Card();
        card.setExpirationDate(LocalDate.now().plusYears(1));
        CardDTO dto = new CardDTO();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.map(card)).thenReturn(dto);

        CardDTO result = cardService.activateCard(1L);

        verify(cardRepository).save(card);
        assertEquals(dto, result);
    }

    @Test
    void setStatusActiveFailedTest() {

        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cardService.activateCard(1L));
    }

    @Test
    void findUserCardsTest() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(1L);

        Card card = new Card();
        CardDTO dto = new CardDTO();

        Page<Card> page = new PageImpl<>(List.of(card));

        when(currentUserCheck.getCurrentUser()).thenReturn(user);
        when(cardSpecification.build(eq(1L), any()))
                .thenReturn(mock(Specification.class));

        when(cardRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        when(cardMapper.map(card)).thenReturn(dto);

        Page<CardDTO> result =
                cardService.findUserCards(0, 10, new CardParamsDTO());

        assertEquals(1, result.getContent().size());
    }

    @Test
    void findCardBalanceTest() {

        Card card = new Card();
        CardBalanceResponseDTO dto = new CardBalanceResponseDTO(1L, "**** **** **** 1234", BigDecimal.valueOf(1000));

        when(cardOwnerCheck.getUserOwnedCard(eq(1L))).thenReturn(card);
        when(cardMapper.mapForBalance(card)).thenReturn(dto);

        CardBalanceResponseDTO result = cardService.findCardBalance(1L);

        assertEquals(dto, result);
    }

    @Test
    void requestBlockTest() {

        Card card = new Card();

        when(cardOwnerCheck.getUserOwnedCard(1L)).thenReturn(card);

        cardService.requestBlock(1L);

        assertEquals(CardStatus.BLOCK_REQUESTED, card.getStatus());
    }
}
