package com.example.bankcards.dto.transaction;

import com.example.bankcards.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResultDTO {

    private BigDecimal fromCardBalance;
    private BigDecimal toCardBalance;
    private TransactionStatus status;
}
