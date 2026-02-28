package com.example.bankcards.dto.card;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CardBalanceResponseDTO {

    private Long id;
    private String maskedNumber;
    private BigDecimal balance;
}
