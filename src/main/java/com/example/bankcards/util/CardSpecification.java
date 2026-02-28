package com.example.bankcards.util;

import com.example.bankcards.dto.card.CardParamsDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CardSpecification {

    public Specification<Card> build(Long ownerId, CardParamsDTO paramsDTO) {
        return hasOwner(ownerId)
                .and(hasStatus(paramsDTO.getStatus()))
                .and(minBalance(paramsDTO.getMinBalance()))
                .and(maxBalance(paramsDTO.getMaxBalance()));
    }

    public Specification<Card> hasOwner(Long ownerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }


    public Specification<Card> hasStatus(CardStatus status) {
        return ((root, query, criteriaBuilder) ->
                status == null ? null :
                criteriaBuilder.equal(root.get("status"), status));
    }

    public Specification<Card> minBalance(BigDecimal min) {
        return ((root, query, criteriaBuilder) ->
                min == null ? null :
                criteriaBuilder.greaterThanOrEqualTo(root.get("balance"), min));
    }

    public Specification<Card> maxBalance(BigDecimal max) {
        return ((root, query, criteriaBuilder) ->
                max == null ? null :
                        criteriaBuilder.lessThanOrEqualTo(root.get("balance"), max));
    }
}
