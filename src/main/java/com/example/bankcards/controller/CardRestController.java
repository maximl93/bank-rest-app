package com.example.bankcards.controller;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.dto.transaction.TransactionRequestDTO;
import com.example.bankcards.dto.transaction.TransactionResultDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
public class CardRestController {

    private final CardService cardService;
    private final TransactionService transactionService;

    @Operation(
            summary = "Найти карты авторизированного пользователя",
            description = "Найти карты пользователя с пагинацией и фильтрацией"
    )
    @GetMapping
    public Page<CardDTO> getMyCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ParameterObject CardParamsDTO paramsDTO
    ) {
        return cardService.findUserCards(page, size, paramsDTO);
    }

    @Operation(
            summary = "Баланс карты",
            description = "Получить баланс карты пользователя по ID"
    )
    @GetMapping("/{cardId}/balance")
    @ResponseStatus(HttpStatus.OK)
    public CardBalanceResponseDTO findCardBalance(@PathVariable("cardId") Long cardId) {
        return cardService.findCardBalance(cardId);
    }

    @Operation(
            summary = "Запросить блок",
            description = "Метод для запроса блокировки карты по ID"
    )
    @PatchMapping("/{cardId}/requestBlock")
    public void requestBlockCard(@PathVariable("cardId") Long cardId) {
        cardService.requestBlock(cardId);
    }

    @Operation(
            summary = "Перевод",
            description = "Перевод средств между двумя картами пользователя"
    )
    @PostMapping("/transfer")
    public TransactionResultDTO transfer(@RequestBody TransactionRequestDTO dto) {
        return transactionService.makeTransaction(dto);
    }
}
