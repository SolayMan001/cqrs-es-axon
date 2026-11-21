package ma.oumouou.cqrsesaxon.query.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ma.oumouou.cqrsesaxon.enums.OperationType;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant date;
    private double amount;
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    private String currency;
    @ManyToOne(optional = false)
    private Account account;

    public AccountOperation(Instant date, double amount, OperationType operationType, String currency, Account account) {
        this.date = date;
        this.amount = amount;
        this.operationType = operationType;
        this.currency = currency;
        this.account = account;
    }
}
