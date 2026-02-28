package com.example.bankcards.entity;

import com.example.bankcards.exception.ForbiddenOperationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Getter
@Setter
public class Card implements BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    private String lastFourDigits;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    private BigDecimal balance;


    public void setActivateStatus() {
        if (isExpired()) {
            throw new ForbiddenOperationException("Нельзя активировать истекшую карту");
        }
        this.status = CardStatus.ACTIVE;
    }

    public void setBlockStatus() {
        if (this.status == CardStatus.EXPIRED) {
            throw new ForbiddenOperationException("Нельзя заблокировать истекшую карту");
        }
        this.status = CardStatus.BLOCKED;
    }

    public void setExpiredStatus() {
        this.status = CardStatus.EXPIRED;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(this.expirationDate);
    }
}
