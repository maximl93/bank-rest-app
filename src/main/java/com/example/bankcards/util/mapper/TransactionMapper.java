package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.transaction.TransactionRequestDTO;
import com.example.bankcards.dto.transaction.TransactionResultDTO;
import com.example.bankcards.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        uses = {ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class TransactionMapper {

    @Mapping(target = "fromCard", source = "fromCardId")
    @Mapping(target = "toCard", source = "toCardId")
    public abstract Transaction map(TransactionRequestDTO dto);

    @Mapping(target = "fromCardBalance", source = "fromCard.balance")
    @Mapping(target = "toCardBalance", source = "toCard.balance")
    public abstract TransactionResultDTO map(Transaction transaction);
}
