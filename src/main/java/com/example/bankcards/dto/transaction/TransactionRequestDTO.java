package com.example.bankcards.dto.transaction;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequestDTO {

    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
}
