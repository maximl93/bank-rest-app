package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardAddDTO;
import com.example.bankcards.dto.card.CardBalanceResponseDTO;
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
import com.example.bankcards.util.CardNumberEncryptor;
import com.example.bankcards.util.mapper.CardMapper;
import com.example.bankcards.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final CardRepository cardRepository;

    private final UserMapper userMapper;

    private final CardMapper cardMapper;

    private final CardNumberEncryptor cardNumberEncryptor;

    private final PasswordEncoder passwordEncoder;

    public UserDTO create(UserCreateDTO createDTO) {
        String email = createDTO.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResourceAlreadyExistsException("User with email " + email + " already exists");
        }
        createDTO.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        User user = userMapper.map(createDTO);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO findById(Long id) {
        return userMapper.map(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public List<UserDTO> findAll() {
        return userMapper.map(userRepository.findAll());
    }

    public UserDTO update(UserUpdateDTO updateDTO, Long id) {
        User updatingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        updateDTO.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        userMapper.update(updateDTO, updatingUser);
        userRepository.save(updatingUser);
        return userMapper.map(updatingUser);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public CardDTO addUserCard(long userId, CardAddDTO addDTO) {
        Card card = userRepository.findById(userId).map(user -> {
            Card addCard = cardRepository.findById(addDTO.getCardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Карта с id: " + addDTO.getCardId() + " нет в БД"));
            if (user.getCards().contains(addCard)) {
                throw new ForbiddenOperationException("Пользователь уже является владельцем данной карты");
            }
            if (addCard.getOwner() != null) {
                throw new ForbiddenOperationException("Другой пользователь является владельцем данной карты");
            }
            user.addCard(addCard);
            userRepository.save(user);
            return addCard;

        }).orElseThrow(() -> new ResourceNotFoundException("Пользователь с id: " + userId + " не найден"));

        return cardMapper.map(card);
    }

    public void deleteUserCard(long userId, long cardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с id: " + userId + " не найден"));
        user.removeCard(cardId);
        userRepository.save(user);
    }

}
