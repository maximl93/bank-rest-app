package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.card.CardBalanceResponseDTO;
import com.example.bankcards.dto.card.CardCreateDTO;
import com.example.bankcards.dto.card.CardDTO;
import com.example.bankcards.dto.card.CardUpdateDTO;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(
        uses = {ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class CardMapper {

    @Mapping(target = "lastFourDigits", source = "number", qualifiedByName = "getLastFourDigits")
    public abstract Card map(CardCreateDTO createDTO);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "maskedNumber", source = "lastFourDigits", qualifiedByName = "getMaskedCardNumber")
    public abstract CardDTO map(Card card);

    public abstract List<CardDTO> map(List<Card> cards);

    public abstract void update(CardUpdateDTO updateDTO, @MappingTarget Card card);

    @Mapping(target = "maskedNumber", source = "lastFourDigits", qualifiedByName = "getMaskedCardNumber")
    public abstract CardBalanceResponseDTO mapForBalance(Card card);

    @Named("getLastFourDigits")
    public String getLastFourDigits(String cardNumber) {
        return cardNumber.substring(cardNumber.length() - 4);
    }

    @Named("getMaskedCardNumber")
    public String getMaskedCardNumber(String lastFourDigits) {
        return "**** **** **** " + lastFourDigits;
    }
}
