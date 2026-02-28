package com.example.bankcards.service;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceAlreadyExistsException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardNumberEncryptor;
import com.example.bankcards.util.CardOwnerCheck;
import com.example.bankcards.util.CardSpecification;
import com.example.bankcards.util.CurrentUserCheck;
import com.example.bankcards.util.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    private final CurrentUserCheck currentUserCheck;

    private final CardOwnerCheck cardOwnerCheck;

    private final CardSpecification cardSpecification;

    private final CardNumberEncryptor cardNumberEncryptor;

    public CardDTO create(CardCreateDTO createDTO) {
        String number = createDTO.getNumber();
        if (cardRepository.findByNumber(cardNumberEncryptor.encrypt(number)).isPresent()) {
            throw new ResourceAlreadyExistsException("Card with number " + number + " already exists");
        }
        Card card = cardMapper.map(createDTO);
        card.setNumber(cardNumberEncryptor.encrypt(createDTO.getNumber()));

        cardRepository.save(card);
        return cardMapper.map(card);
    }

    public CardDTO findById(Long id) {
        return cardMapper.map(cardOwnerCheck.getUserOwnedCard(id));
    }

    public List<CardDTO> findAll() {
        return cardMapper.map(cardRepository.findAll());
    }

    public CardDTO update(CardUpdateDTO updateDTO, Long id) {
        Card updatingCard = cardOwnerCheck.getCardOrThrowException(id);
        cardMapper.update(updateDTO, updatingCard);
        cardRepository.save(updatingCard);
        return cardMapper.map(updatingCard);
    }

    public void deleteById(Long id) {
        cardRepository.deleteById(id);
    }

    public CardDTO activateCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Карты с id: " + id + "нет в базе данных"));
        card.setActivateStatus();
        cardRepository.save(card);
        return cardMapper.map(card);
    }

    public CardDTO blockCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Карты с id: " + id + "нет в базе данных"));
        card.setBlockStatus();
        cardRepository.save(card);
        return cardMapper.map(card);
    }

    public Page<CardDTO> findUserCards(int page, int size, CardParamsDTO paramsDTO) {
        User currentUser = currentUserCheck.getCurrentUser();
        Specification<Card> spec = cardSpecification.build(currentUser.getId(), paramsDTO);

        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cards = cardRepository.findAll(spec, pageable);

        return cards.map(cardMapper::map);
    }

    public CardBalanceResponseDTO findCardBalance(Long cardId) {
        Card card = cardOwnerCheck.getUserOwnedCard(cardId);
        return cardMapper.mapForBalance(card);

    }

    public void requestBlock(Long cardId) {
        Card card = cardOwnerCheck.getUserOwnedCard(cardId);
        card.setStatus(CardStatus.BLOCK_REQUESTED);
    }

}
