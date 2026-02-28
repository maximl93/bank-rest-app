package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardAddDTO;
import com.example.bankcards.dto.card.CardDTO;
import com.example.bankcards.dto.user.UserCreateDTO;
import com.example.bankcards.dto.user.UserDTO;
import com.example.bankcards.dto.user.UserUpdateDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.exception.ResourceAlreadyExistsException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtAuthFilter;
import com.example.bankcards.security.JwtProvider;
import com.example.bankcards.util.CardNumberEncryptor;
import com.example.bankcards.util.mapper.CardMapper;
import com.example.bankcards.util.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CardNumberEncryptor cardNumberEncryptor;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void createNewUserSuccessfullyTest() {

        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("test@mail.com");
        createDTO.setPassword("123");

        User user = new User();
        UserDTO response = new UserDTO();

        when(userRepository.findByEmail(createDTO.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(createDTO.getPassword()))
                .thenReturn("encodedPass");

        when(userMapper.map(createDTO))
                .thenReturn(user);

        when(userMapper.map(user))
                .thenReturn(response);

        UserDTO result = userService.create(createDTO);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void createNewUserWithExistEmailTest() {

        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("test@mail.com");

        when(userRepository.findByEmail(createDTO.getEmail()))
                .thenReturn(Optional.of(new User()));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.create(createDTO));

        verify(userRepository, never()).save(any());
    }

    @Test
    void findByIdSuccessTest() {

        User user = new User();
        UserDTO dto = new UserDTO();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.map(user))
                .thenReturn(dto);

        UserDTO result = userService.findById(1L);

        assertEquals(dto, result);
    }

    @Test
    void findByIdFailedTest() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.findById(1L));
    }

    @Test
    void updateUserTest() {

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setPassword("newPassword");

        User user = new User();
        UserDTO dto = new UserDTO();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(updateDTO.getPassword()))
                .thenReturn("encodedNewPassword");

        when(userMapper.map(user)).thenReturn(dto);

        UserDTO result = userService.update(updateDTO, 1L);

        verify(userMapper).update(updateDTO, user);
        verify(userRepository).save(user);
        assertEquals(dto, result);
    }

    @Test
    void addUserCardTest() {

        User user = new User();
        Card card = new Card();
        CardDTO dto = new CardDTO();

        CardAddDTO addDTO = new CardAddDTO();
        addDTO.setCardId(10L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cardRepository.findById(10L))
                .thenReturn(Optional.of(card));

        when(cardMapper.map(card))
                .thenReturn(dto);

        CardDTO result = userService.addUserCard(1L, addDTO);

        verify(userRepository).save(user);
        assertEquals(dto, result);
    }

    @Test
    void addCardUserAlreadyOwnsTest() {

        User user = new User();
        Card card = new Card();

        user.addCard(card);

        CardAddDTO addDTO = new CardAddDTO();
        addDTO.setCardId(10L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cardRepository.findById(10L))
                .thenReturn(Optional.of(card));

        assertThrows(ForbiddenOperationException.class,
                () -> userService.addUserCard(1L, addDTO));

        verify(userRepository, never()).save(any());
    }

    @Test
    void addUserCardWithUserNotFoundTest() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        CardAddDTO addDTO = new CardAddDTO();
        addDTO.setCardId(10L);

        assertThrows(ResourceNotFoundException.class,
                () -> userService.addUserCard(1L, addDTO));
    }

    @Test
    void deleteUserCard_shouldRemoveCardAndSaveUser() {

        User user = new User();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUserCard(1L, 10L);

        verify(userRepository).save(user);
    }
}
