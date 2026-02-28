package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardOwnerCheck {


    private final CardRepository cardRepository;
    private final CurrentUserCheck currentUserCheck;

    public Card getCardOrThrowException(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Карта c id: " + cardId + " не найдена"));
    }

    public Card getUserOwnedCard(Long cardId) {
        Card card = getCardOrThrowException(cardId);

        User currentUser = currentUserCheck.getCurrentUser();

        if (!card.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenOperationException("Нет доступа к карте с id: " + cardId);
        }

        return card;
    }
}
