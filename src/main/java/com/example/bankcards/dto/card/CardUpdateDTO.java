package com.example.bankcards.dto.card;

import com.example.bankcards.entity.CardStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CardUpdateDTO {

    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;
}
