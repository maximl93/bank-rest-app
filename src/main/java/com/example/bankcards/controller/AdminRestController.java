package com.example.bankcards.controller;


import com.example.bankcards.dto.card.CardAddDTO;
import com.example.bankcards.dto.card.CardCreateDTO;
import com.example.bankcards.dto.card.CardDTO;
import com.example.bankcards.dto.card.CardUpdateDTO;
import com.example.bankcards.dto.user.UserCreateDTO;
import com.example.bankcards.dto.user.UserDTO;
import com.example.bankcards.dto.user.UserUpdateDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final UserService userService;
    private final CardService cardService;

    // Управление пользователями администратором

    @Operation(
            summary = "Создать нового пользователя"
    )
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody UserCreateDTO createData) {
        return userService.create(createData);
    }

    @Operation(
            summary = "Найти пользователя",
            description = "Найти пользователя в БД по ID"
    )
    @GetMapping("/users/{userId}")
    public UserDTO findUserById(@PathVariable("userId") Long id) {
        return userService.findById(id);
    }

    @Operation(
            summary = "Найти всех пользователей"
    )
    @GetMapping("/users")
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @Operation(
            summary = "Обновить пользователя",
            description = "Обновить уже созданного пользователя в БД"
    )
    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@PathVariable("userId") long userId, @RequestBody UserUpdateDTO updateDTO){
        return userService.update(updateDTO, userId);
    }

    @Operation(
            summary = "Удалить пользователя из БД по ID"
    )
    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("userId") long userId) {
        userService.deleteById(userId);
    }

    @Operation(
            summary = "Добавить пользователю карту"
    )
    @PostMapping("/users/{userId}/cards")
    @ResponseStatus(HttpStatus.OK)
    public CardDTO addUserCard(@PathVariable("userId") long userId, @RequestBody CardAddDTO addDTO) {
        return userService.addUserCard(userId, addDTO);
    }

    @Operation(
            summary = "Удалить пользователю карту"
    )
    @DeleteMapping("/users/{userId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserCard(@PathVariable("userId") long userId, @PathVariable("cardId") long cardId) {
        userService.deleteUserCard(userId, cardId);
    }

    // Управление картами


    @Operation(
            summary = "Создать новую карту"
    )
    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CardDTO createCard(@RequestBody CardCreateDTO dto) {
        return cardService.create(dto);
    }

    @Operation(
            summary = "Обновить карту в БД"
    )
    @PutMapping("/cards/{cardId}")
    public CardDTO updateCard(@RequestBody CardUpdateDTO dto, @PathVariable("cardId") Long cardId) {
        return cardService.update(dto, cardId);
    }

    @Operation(
            summary = "Блокировать карту в БД"
    )
    @PutMapping("/cards/{cardId}/block")
    public CardDTO blockCard(@PathVariable("cardId") Long cardId) {
        return cardService.blockCard(cardId);
    }

    @Operation(
            summary = "Активировать карту в БД"
    )
    @PutMapping("/cards/{cardId}/activate")
    public CardDTO activateCard(@PathVariable("cardId") Long cardId) {
        return cardService.activateCard(cardId);
    }

    @Operation(
            summary = "Удалить карту из БД"
    )
    @DeleteMapping("/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable("cardId") Long cardId) {
        cardService.deleteById(cardId);
    }

    @Operation(
            summary = "Получить все карты из БД"
    )
    @GetMapping("/cards")
    public List<CardDTO> getAllCards() {
        return cardService.findAll();
    }

    @Operation(
            summary = "Найти карту в БД по ID"
    )
    @GetMapping("/cards/{cardId}")
    public CardDTO findCardById(@PathVariable("cardId") Long cardId) {
        return cardService.findById(cardId);
    }

}
