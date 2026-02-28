package com.example.bankcards.dto.card;

import com.example.bankcards.entity.CardStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class CardParamsDTO {

    private CardStatus status;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;
}
