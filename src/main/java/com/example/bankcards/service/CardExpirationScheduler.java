package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class CardExpirationScheduler {

    private final CardRepository repository;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void expireCards() {
        List<Card> expiredCards = repository.findByStatusAndExpirationDateBefore(
                CardStatus.ACTIVE,
                LocalDate.now()
        );
        for (Card card : expiredCards) {
            card.setExpiredStatus();
        }
    }
}
