package com.example.bankcards.service;

import com.example.bankcards.dto.transaction.TransactionRequestDTO;
import com.example.bankcards.dto.transaction.TransactionResultDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.CurrentUserCheck;
import com.example.bankcards.util.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CurrentUserCheck currentUserCheck;

    @Transactional
    public TransactionResultDTO makeTransaction(TransactionRequestDTO dto) {
        if (dto.getFromCardId().equals(dto.getToCardId())) {
            throw new ForbiddenOperationException("Нельзя перевести средства на ту же карту");
        }

        User currentUser = currentUserCheck.getCurrentUser();

        Card fromCard = cardRepository.findByIdForUpdate(dto.getFromCardId(), currentUser.getId())
                .orElseThrow(() -> new  ForbiddenOperationException("Карта не найдена или пользователю запрещен к ней доступ"));
        Card toCard = cardRepository.findByIdForUpdate(dto.getToCardId(), currentUser.getId())
                .orElseThrow(() -> new ForbiddenOperationException("Карта не найдена или пользователю запрещен к ней доступ"));

        if (fromCard.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new ForbiddenOperationException("Недостаточно средств для списания");
        }

        Transaction transaction = createPendingTransaction(dto);

        try {
            fromCard.setBalance(fromCard.getBalance().subtract(dto.getAmount()));
            toCard.setBalance(toCard.getBalance().add(dto.getAmount()));

            updateTransactionStatus(transaction.getId(), TransactionStatus.SUCCESS);
        } catch (Exception e) {
            updateTransactionStatus(transaction.getId(), TransactionStatus.FAILED);
        }

        return transactionMapper.map(transaction);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private Transaction createPendingTransaction(TransactionRequestDTO dto) {
        Transaction pendingTransaction = transactionMapper.map(dto);
        pendingTransaction.setCreatedAt(LocalDateTime.now());
        pendingTransaction.setStatus(TransactionStatus.PENDING);
        return transactionRepository.save(pendingTransaction);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void updateTransactionStatus(Long transactionId, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Транзакция не найдена в БД"));
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }
}
